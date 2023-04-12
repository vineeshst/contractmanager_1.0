import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { config } from 'rxjs';
import { IUser, User, UserSelection } from '../../../../admin/user-management/user-management.model';
import { UserGroup } from '../../../models/user-group';
import { AddUserDialogComponent } from '../../../shared/add-user-dialog/add-user-dialog.component';
import { ConfirmationDialogComponent } from '../../../shared/confirmation-dialog/confirmation-dialog.component';
import { DisplayMode } from '../../../shared/constants/contract-manager.constants';
import { UserManagementService } from '../../user-management.service';
import { TranslateService } from '@ngx-translate/core';
import { Roles } from '../../../shared/constants/user-role.constans';

@Component({
  selector: 'jhi-add-user-group',
  templateUrl: './add-user-group.component.html',
  styleUrls: ['./add-user-group.component.scss'],
})
export class AddUserGroupComponent implements OnInit {
  selectedUsers?: UserSelection[];
  displayMode!: DisplayMode;
  userGroupId?: string = undefined;
  userGroup?: UserGroup = undefined;
  entityRole = 'PRIMARY_OWNER'; //TODO

  userGroupForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    description: [''],
  });

  get userGroupName(): AbstractControl | null {
    return this.userGroupForm.get('name');
  }
  /**
   *
   */
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
      this.userGroupId = paramMap.get('id') ?? undefined;

      if (this.userGroupId) {
        this.initEditMode(this.userGroupId);
        this.displayMode = DisplayMode.VIEW;
      } else {
        this.initCreateMode();
        this.displayMode = DisplayMode.CREATE;
      }
    });
  }

  initCreateMode(): void {
    this.userGroup = new UserGroup();
  }

  initEditMode(userGroupId: any): void {
    this.userManagementService.getUserGroup(userGroupId).subscribe(userGroup => {
      this.updateUserGroupForm(userGroup);
    });
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

  save(): void {
    const userGroup = this.updateUserGroup(this.userGroupForm.value);
    if (this.userGroupId) {
      this.userManagementService.updateUserGroup(this.userGroupId, userGroup).subscribe(res => {
        this.showMessage(`User Group is updated successfully!`, 'Ok');
        this.ngOnInit();
      });
    } else {
      this.userManagementService.createUserGroup(userGroup).subscribe(res => {
        if (res.id) {
          this.showMessage(`User Group ${res.name ?? ''} created successfully!`, 'Ok');
          this.router.navigate([`contractmanager/user-management/user-groups/view-user-group/${res.id}`]);
        }
      });
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

        if (this.userGroup?.id && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.userManagementService.deleteUserGroup(this.userGroup.id).subscribe(response => {
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

  private updateUserGroup(formValues: any): UserGroup {
    let userGroup = new UserGroup();
    if (formValues) {
      userGroup = Object.assign(userGroup, this.userGroupForm.value) as UserGroup;
      userGroup.members = this.selectedUsers?.filter(u => u.isSelected) ?? [];
      userGroup.id = this.userGroupId;
    }
    return userGroup;
  }

  private updateUserGroupForm(userGroup: UserGroup): void {
    this.userGroupForm.patchValue(Object.assign({}, userGroup));
    this.selectedUsers = userGroup.members.map(u => Object.assign(u, { isSelected: true }) as UserSelection);
  }

  private showMessage(message: string, action: string): void {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }
}
