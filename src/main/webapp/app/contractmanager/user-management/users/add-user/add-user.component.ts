import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { IUser, User } from '../../../../core/user/user.model';
import { Roles } from '../../../shared/constants/user-role.constans';
import { UserService } from '../../../../core/user/user.service';
import { AccountService } from '../../../../core/auth/account.service';
import { UserManagementService } from '../../user-management.service';
import { SecurityGroup } from '../../../models/security-group';
import { ActivatedRoute, Router } from '@angular/router';
import { DisplayMode } from '../../../shared/constants/contract-manager.constants';
import { UserManagementService as AdminUserManagementService } from '../../../../admin/user-management/service/user-management.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserGroup } from '../../../models/user-group';
import { NotificationConfig } from '../../../models/notifications-config';
import { ConfirmationDialogComponent } from '../../../shared/confirmation-dialog/confirmation-dialog.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss'],
})
export class AddUserComponent implements OnInit {
  user!: User;
  userId?: string;
  currentUser: IUser = new User();
  isEditMode = true;
  securityGroups: SecurityGroup[] = [];
  userGroups: UserGroup[] = [];
  displayMode!: DisplayMode;
  entityRole = 'PRIMARY_OWNER';

  notificationConfig: NotificationConfig = new NotificationConfig();
  notificationConfigForm: FormGroup = this.fb.group({
    notifications: this.fb.array([]),
  });

  userForm = this.fb.group({
    id: [],
    login: [''],
    firstName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    lastName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.minLength(8), Validators.maxLength(15)]],
    externalIdentifier: [''],
    typeOfUser: [''],
    organisationUnit: [{ value: 'Default', disabled: true }],
    administrator: [false],
    authorisedSignatory: [false],
    departmentId: [''],
    signatoryAgent: [false],
    hierarchyLevel: [''],
    securityGroup: [null],
    expiryDate: [''],
    userGroup: [null],
    supervisorUser: [''],
    preference: this.fb.group({
      recordsPerPage: [''],
      gridChoice: [''],
      recordsLayout: [''],
      dashboardLayout: [''],
      searchPanelVisible: [false],
      openRecordInSameTab: [false],
      language: [''],
      timeZone: [''],
      dateFormat: [''],
      timeFormat: [''],
      preferredCurrency: [''],
    }),
  });

  teamForm = this.fb.group({
    primaryOwner: [],
    secondaryOwner: [],
  });

  get preferenceForm(): FormGroup | null {
    return this.userForm.get('preference') as FormGroup;
  }

  get firstName(): FormControl {
    return this.userForm.get('firstName') as FormControl;
  }

  get lastName(): FormControl {
    return this.userForm.get('lastName') as FormControl;
  }

  get email(): FormControl {
    return this.userForm.get('email') as FormControl;
  }

  teamFields: string[] = [Roles.SecondaryOwner];

  assignedUsers: any[] = [];

  @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;

  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private userService: UserService,
    private accoutService: AccountService,
    private userManagementService: UserManagementService,
    private route: ActivatedRoute,
    private adminUserManagementService: AdminUserManagementService,
    private snackBar: MatSnackBar,
    private router: Router,
    private translate: TranslateService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(next => {
      const paramMap = this.route.snapshot.paramMap;
      this.userId = paramMap.get('id') ?? undefined;

      if (this.userId) {
        this.initEditMode(this.userId);
        this.displayMode = DisplayMode.VIEW;
      } else {
        this.initCreateMode();
        this.displayMode = DisplayMode.CREATE;
      }
    });

    this.userManagementService.getSecurityGroups().subscribe(securityGroups => {
      this.securityGroups = securityGroups;
    });

    this.userManagementService.getUserGroups().subscribe(userGroups => {
      this.userGroups = userGroups;
    });

    this.accoutService.identity().subscribe(account => {
      this.currentUser = Object.assign(this.currentUser, account);

      this.teamForm.patchValue({
        primaryOwner: this.currentUser,
      });
      this.assignedUsers.push({ firstName: this.currentUser.firstName, lastName: this.currentUser.lastName, role: Roles.PrimaryOwner });
    });
  }
  initCreateMode(): void {
    this.user = new User();
  }

  initEditMode(userId: string): void {
    this.adminUserManagementService.find(userId).subscribe(user => {
      this.updateUserForm(user);
    });
  }

  updateUserForm(user: any): void {
    this.userForm.patchValue(Object.assign({}, user));
    this.userId = user.id;
    this.user = user;
  }

  save(): void {
    this.updateUser(this.user);

    if (this.userId) {
      this.userService.update(this.user).subscribe(res => {
        this.showMessage(`User is updated successfully!`, 'Ok');
        this.ngOnInit();
      });
    } else {
      this.user.authorities = [];
      this.user.authorities.push('ROLE_USER');
      this.user.authorities.push('ROLE_ADMIN');
      this.userService.create(this.user).subscribe(
        res => {
          this.showMessage(`User created successfully!`, 'Ok');
          this.router.navigate([`/contractmanager/user-management/user/view-user/${res.login ?? ''}`]);
        },
        err => {
          if (err.status === 400) {
            this.showMessage(`Email Address already exists!`, 'Ok');
          }
        }
      );
    }
  }

  openTeamManagementDialog(mode: string): void {
    this.dialog
      .open(this.teamDialog, {
        width: '70%',
        minHeight: '90%',
      })
      .afterClosed()
      .subscribe(() => {
        this.createAssignedUserList();
      });
  }

  deleteMember(user: any): void {
    if (!user.role) {
      return;
    }

    const team = this.teamForm.get(user.role)?.value as IUser[];
    const index = team.findIndex(x => x.id === user.user.id);

    if (index >= 0) {
      team.splice(index, 1);
    }
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

        if (this.user.login && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.adminUserManagementService.delete(this.user.login).subscribe(response => {
            this.showConfirmationMessage('User Deleted Successfully');
            this.router.navigate(['/contractmanager/user-management/user/view-users']);
          });
        }
      });
  }

  private showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }

  private navigateBack(): void {
    this.location.back();
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

  private updateUser(user?: User): void {
    user = Object.assign(user, this.userForm.value);
    user!.login = this.email.value;
    user!.langKey = user?.preference?.language;
  }

  private showMessage(message: string, action: string): void {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }
}
