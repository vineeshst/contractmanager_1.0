import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { IUser, UserSelection } from '../../../../admin/user-management/user-management.model';
import { ContractType } from '../../../models/contractType';
import { Privilege, Privileges, SecurityGroup } from '../../../models/security-group';
import { AddUserDialogComponent } from '../../../shared/add-user-dialog/add-user-dialog.component';
import { ConfirmationDialogComponent } from '../../../shared/confirmation-dialog/confirmation-dialog.component';
import { DisplayMode } from '../../../shared/constants/contract-manager.constants';
import { Roles } from '../../../shared/constants/user-role.constans';
import { UserManagementService } from '../../user-management.service';

@Component({
  selector: 'jhi-add-security-group',
  templateUrl: './add-security-group.component.html',
  styleUrls: ['./add-security-group.component.scss'],
})
export class AddSecurityGroupComponent implements OnInit {
  securityGroupId?: string | null;
  securityGroup: SecurityGroup | undefined = undefined;
  displayMode!: DisplayMode;
  selectedUsers?: UserSelection[];
  entityRole = 'PRIMARY_OWNER';

  public kpiToggleClass = true;
  public reportToggleClass = true;
  public privilegeToggleClass = true;

  kpiDataSource = new MatTableDataSource<any>([]);
  reportDataSource = new MatTableDataSource<any>([]);
  privilegesDataSource = new MatTableDataSource<any>([]);

  assignedUsers: any[] = [];

  privileges!: Privileges;

  teamFields: string[] = [Roles.SecondaryOwner];
  kpiColumns: string[] = ['kpi', 'view', 'none'];
  reportColumns: string[] = ['reports', 'view', 'none'];
  privilegeColumns: string[] = ['privilege', 'view', 'manage', 'none'];

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;

  get securityGroupName(): AbstractControl | null {
    return this.securityGroupForm.get('name');
  }

  securityGroupForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    description: [''],
    kpi: this.fb.array([]),
    report: this.fb.array([]),
    privilege: this.fb.array([]),
  });

  teamForm = this.fb.group({
    primaryOwner: [],
    secondaryOwner: [],
  });

  kpi = this.securityGroupForm.get('kpi') as FormArray;
  report = this.securityGroupForm.get('report') as FormArray;
  privilege = this.securityGroupForm.get('privilege') as FormArray;

  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private userManagementService: UserManagementService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(next => {
      const paramMap = this.route.snapshot.paramMap;
      this.securityGroupId = paramMap.get('id');

      if (this.securityGroupId) {
        this.initEditMode(this.securityGroupId);
        this.displayMode = DisplayMode.VIEW;
      } else {
        this.initCreateMode();
        this.displayMode = DisplayMode.CREATE;
      }
    });
  }

  save(): void {
    if (!this.securityGroup) {
      this.securityGroup = new SecurityGroup();
    }

    this.updateSecurityGroup(this.securityGroup);

    if (this.displayMode === DisplayMode.CREATE) {
      this.userManagementService.createSecurityGroup(this.securityGroup).subscribe(response => {
        this.showConfirmationMessage('Security Group Created Successfully!');
        this.router.navigate([`/contractmanager/user-management/security-group/view-security-group/${response.id ?? ''}`]);
      });
    } else {
      this.userManagementService.updateSecurityGroup(this.securityGroup).subscribe(response => {
        this.showConfirmationMessage('Security Group Updated Successfully!');
        this.ngOnInit();
      });
    }
  }

  openAddUserDialog(): void {
    this.dialog
      .open(AddUserDialogComponent, {
        width: '70%',
        height: '90vh',
        data: this.selectedUsers,
      })
      .afterClosed()
      .subscribe(data => {
        if (data?.isModified && data?.selectedUsers) {
          this.selectedUsers = data.selectedUsers;
        }
      });
  }

  switchToEditMode(): void {
    this.displayMode = DisplayMode.UPDATE;
  }

  cancel(mode: string): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: this.translate.instant('contractmanager.message.cancelConfirmation'),
          dialogType: 'cancel',
        },
        height: '130px',
      })
      .afterClosed()
      .subscribe((confirmation: boolean) => {
        if (!confirmation) {
          return;
        }

        if (mode === 'create') {
          this.navigateBack();
        } else {
          this.ngOnInit();
        }
      });
  }

  delete(): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: this.translate.instant('contractmanager.message.deleteConfirmation'),
          dialogType: 'delete',
        },
        height: '130px',
      })
      .afterClosed()
      .subscribe((confirmation: boolean) => {
        if (!confirmation) {
          return;
        }

        if (this.securityGroupId && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.userManagementService.deleteSecurityGroup(this.securityGroupId).subscribe(response => {
            this.showConfirmationMessage('User Deleted Successfully');
            this.router.navigate(['/contractmanager/user-management/user/view-users']);
          });
        }
      });
  }

  private navigateBack(): void {
    this.location.back();
  }

  private initCreateMode(): void {
    this.userManagementService.getMetaPrivileges().subscribe((securityGroup: SecurityGroup) => {
      this.loadTableSource({
        kpi: securityGroup.kpi,
        report: securityGroup.report,
        privileges: securityGroup.privilege,
        contractTypes: securityGroup.contractTypes,
      });
    });
  }

  private initEditMode(securityGroupId: string): void {
    this.userManagementService.getSecurityGroup(securityGroupId).subscribe((securityGroup: SecurityGroup) => {
      this.updateSecurityGroupForm(securityGroup);
      this.loadTableSource({ kpi: securityGroup.kpi, report: securityGroup.report, privileges: securityGroup.privilege });
    });
  }

  private loadTableSource({
    kpi,
    report,
    privileges,
    contractTypes,
  }: { kpi?: any[]; report?: Privilege[]; privileges?: Privilege[]; contractTypes?: ContractType[] } = {}): void {
    if (kpi) {
      this.kpiDataSource = new MatTableDataSource(kpi);
    }

    if (report) {
      this.reportDataSource = new MatTableDataSource(report);
    }

    if (privileges) {
      if (contractTypes) {
        const ctPrivileges: Privilege[] = [];
        contractTypes.forEach(contractType => {
          const privilege = new Privilege();
          privilege.name = contractType.name;
          privilege.privilege = Privileges.NONE;
          ctPrivileges.push(privilege);
        });
        privileges.push(...ctPrivileges);
      }
      this.privilegesDataSource = new MatTableDataSource(privileges);
    }

    this.kpi.clear();
    this.report.clear();
    this.privilege.clear();

    this.kpiDataSource.data.forEach((value: Privilege) => this.formArrayCallBack(this.kpi, value));

    this.reportDataSource.data.forEach((value: Privilege) => this.formArrayCallBack(this.report, value));

    this.privilegesDataSource.data.forEach((value: Privilege) => this.formArrayCallBack(this.privilege, value));
  }

  private formArrayCallBack(formArray: FormArray, value: Privilege): void {
    formArray.push(
      this.fb.group({
        name: value.name,
        privilege: isNaN(value.privilege) ? Privileges[value.privilege].toString() : value.privilege.toString(),
      })
    );
  }

  private createAssignedUserList(): void {
    this.assignedUsers = [];

    const teamForm = this.teamForm.value;

    if (teamForm?.primaryOwner) {
      const primaryOwner = teamForm.primaryOwner;
      this.assignedUsers.push({ firstName: primaryOwner.firstName, lastName: primaryOwner.lastName, role: Roles.PrimaryOwner });
    }
    if (teamForm?.secondaryOwner) {
      const secondaryOwner = teamForm.secondaryOwner as IUser[];
      secondaryOwner.forEach(user => {
        this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: Roles.SecondaryOwner, id: user.id });
      });
    }
  }

  private showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }

  private updateSecurityGroup(securityGroup: SecurityGroup): void {
    securityGroup.id = this.securityGroupId;
    securityGroup = Object.assign(securityGroup, this.securityGroupForm.value);
    securityGroup.members = this.selectedUsers?.filter(u => u.isSelected) ?? [];
  }

  private updateSecurityGroupForm(securityGroup: SecurityGroup): void {
    this.securityGroupForm.patchValue(Object.assign({}, securityGroup));
    this.selectedUsers = securityGroup.members?.map(u => Object.assign(u, { isSelected: true }) as UserSelection);
  }
}
