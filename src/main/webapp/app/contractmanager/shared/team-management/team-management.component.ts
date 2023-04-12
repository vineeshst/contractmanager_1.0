import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { IUser, UserSelection } from '../../../admin/user-management/user-management.model';
import { AccountService } from '../../../core/auth/account.service';
import { MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../../entities/user/user.service';
import { UserManagementService as AdminUserManagementService } from '../../../admin/user-management/service/user-management.service';
import { UserManagementService } from '../../user-management/user-management.service';
import { Roles } from '../constants/user-role.constans';
import { Team } from '../../models/team';
import { Account } from '../../../core/auth/account.model';
import { UserGroup, UserGroupSelection } from '../../models/user-group';
import { TeamGroups } from '../../models/team-groups';
import { NotificationConfig, NotificationMap } from '../../models/notifications-config';

@Component({
  selector: 'jhi-team-management',
  templateUrl: './team-management.component.html',
  styleUrls: ['./team-management.component.scss'],
})
export class TeamManagementComponent {
  //users: IUser[] | null | undefined;
  assignedUsers: IUser[] = [];
  currUserEmail: string | undefined;
  currentUser: IUser | undefined;

  users?: UserSelection[];
  userGroups?: UserGroupSelection[];

  total = 0;

  selectedUsers: UserSelection[] = [];
  userSearchKey = '';
  team = new Team();
  teamGroup = new TeamGroups();
  userConfig?: NotificationMap;

  get selectedUserCount(): number {
    return (this.userGroups?.filter(u => u.selectedRole).length ?? 0) + (this.users?.filter(u => u.selectedRole).length ?? 0);
  }

  @Input() teamForm?: FormGroup;
  @Input() teamGroupForm?: FormGroup;
  @Input() fields: string[] | undefined;
  @Input() notificationConfigForm!: FormGroup;
  @Input() notificationConfig!: NotificationConfig;

  constructor(
    private adminManagementService: AdminUserManagementService,
    private userManagementService: UserManagementService,
    private accountService: AccountService,
    private fb: FormBuilder
  ) {}

  get roles(): typeof Roles {
    return Roles;
  }

  get notifications(): FormArray {
    return this.notificationConfigForm.get('notifications') as FormArray;
  }

  ngOnInit(): void {
    this.userManagementService.getMetaUserRoles().subscribe(userRoles => {
      this.fields = userRoles.filter(x => this.fields!.includes(x.id)).map(x => x.id);
    });

    this.patchFormValues();
  }

  patchFormValues(): void {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    this.accountService.identity().subscribe((account: Account) => {
      this.currUserEmail = account.email;
    });

    const notificationMap = this.notificationConfig.userConfig;
    if (notificationMap) {
      this.userConfig = notificationMap;
    }

    this.adminManagementService.query().subscribe(data => {
      this.users = data.body as UserSelection[];

      this.users.forEach(user => {
        //formArray.push(this.fb.group([{enabled: true}]));

        if (user.email === this.currUserEmail) {
          this.currentUser = user;
        }
      });

      Object.entries(this.teamForm?.value).forEach(prop => {
        if (prop[0] === 'primaryOwner' || prop[1] === null) {
          return;
        }

        const members = prop[1] as IUser[];
        if (members.length > 0) {
          members.forEach(member => {
            const selectedUser = this.users?.find(user => user.id === member.id);

            if (selectedUser) {
              selectedUser.selectedRole = this.getPropertyReverse(prop[0]);
            }
          });
        }
      });

      this.users.sort((a, b) => {
        if (!a.selectedRole) {
          return 1;
        }
        return -1;
      });
    });

    this.userManagementService.getUserGroups().subscribe(userGroups => {
      this.userGroups = userGroups;
      this.userGroups.forEach(ug => {
        const id = ug.id;
        if (id) {
          this.notifications.push(
            this.fb.group({
              groupId: [ug.id, Validators.required],
              enabled: this.isNotificationEnabled(id),
            })
          );
        }
      });

      if (this.teamGroupForm) {
        Object.entries(this.teamGroupForm.value).forEach(prop => {
          if (prop[1] === null) {
            return;
          }

          const members = prop[1] as UserGroup[];
          if (members.length > 0) {
            members.forEach(member => {
              const selectedgroup = this.userGroups?.find(group => group.id === member.id);

              if (selectedgroup) {
                selectedgroup.selectedRole = this.getPropertyReverse(prop[0]);
              }
            });
          }
        });
      }
    });
  }

  save(): void {
    if (!this.teamForm) {
      this.teamForm = this.fb.group({});
    }
    this.teamForm.patchValue({
      primaryOwner: this.currentUser,
    });

    this.users
      ?.filter(x => x.selectedRole)
      .forEach(user => {
        this.team[this.getProperty(user.selectedRole) as keyof Team].push(user);
      });

    this.userGroups
      ?.filter(x => x.selectedRole)
      .forEach(userGroup => {
        this.teamGroup[this.getProperty(userGroup.selectedRole) as keyof TeamGroups].push(userGroup);
      });

    this.teamForm.patchValue(this.team);
    this.teamGroupForm?.patchValue(this.teamGroup);
  }

  getValue(role: string): string {
    switch (role) {
      case Roles.PrimaryOwner:
        return 'Primary Owner';

      case Roles.ContractAdmin:
        return 'Contract Admin';

      case Roles.Contributor:
        return 'Contributor';

      case Roles.DeviationApprover:
        return 'Deviation Approver';

      case Roles.ExternalReviewer:
        return 'External Reviewer';

      case Roles.ExternalSignatory:
        return 'External Signatory';

      case Roles.InternalSignatory:
        return 'Internal Signatory';

      case Roles.Observer:
        return 'Observer';

      case Roles.Reviewer:
        return 'Reviewer';

      case Roles.SecondaryOwner:
        return 'Secondary Owner';

      case Roles.Approver:
        return 'Approver';
      default:
        return 'N/A';
    }
  }

  private getProperty(role?: Roles): string {
    switch (role) {
      case Roles.PrimaryOwner:
        return 'primaryOwner';

      case Roles.ContractAdmin:
        return 'contractAdmin';

      case Roles.Contributor:
        return 'contributor';

      case Roles.DeviationApprover:
        return 'deviationApprover';

      case Roles.ExternalReviewer:
        return 'externalReviewer';

      case Roles.ExternalSignatory:
        return 'externalSignatory';

      case Roles.InternalSignatory:
        return 'internalSignatory';

      case Roles.Observer:
        return 'observer';

      case Roles.Reviewer:
        return 'reviewer';

      case Roles.SecondaryOwner:
        return 'secondaryOwner';

      case Roles.Approver:
        return 'approver';

      default:
        return '';
    }
  }

  private getPropertyReverse(role?: string): Roles {
    switch (role) {
      case 'primaryOwner':
        return Roles.PrimaryOwner;

      case 'contractAdmin':
        return Roles.ContractAdmin;

      case 'contributor':
        return Roles.Contributor;

      case 'deviationApprover':
        return Roles.DeviationApprover;

      case 'externalReviewer':
        return Roles.ExternalReviewer;

      case 'externalSignatory':
        return Roles.ExternalSignatory;

      case 'internalSignatory':
        return Roles.InternalSignatory;

      case 'observer':
        return Roles.Observer;

      case 'reviewer':
        return Roles.Reviewer;

      case 'secondaryOwner':
        return Roles.SecondaryOwner;

      case 'approver':
        return Roles.Approver;

      default:
        return Roles.None;
    }
  }

  private isNotificationEnabled(id: string): boolean {
    return !this.userConfig || !id || !Object.prototype.hasOwnProperty.call(this.userConfig, id) ? true : this.userConfig[id];
  }
}
