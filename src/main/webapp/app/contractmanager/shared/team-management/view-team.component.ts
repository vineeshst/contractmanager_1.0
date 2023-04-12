import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { AccountService } from '../../../core/auth/account.service';
import { Account } from '../../../core/auth/account.model';
import { IUser, User } from '../../../admin/user-management/user-management.model';
import { FormGroup } from '@angular/forms';
import { Role } from '../../models/role';
import { Roles } from '../constants/user-role.constans';
import { UserGroup } from '../../models/user-group';

@Component({
  selector: 'jhi-view-team',
  templateUrl: './view-team.component.html',
  styleUrls: ['./view-team.component.scss'],
})
export class ViewTeamComponent implements OnInit {
  @Input() assignedUsers: Role[] | undefined;

  @Output() deleteMember = new EventEmitter<any>();

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    if (!this.assignedUsers) {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      this.accountService.identity().subscribe((account: Account) => {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        this.assignedUsers.push({ firstName: account.firstName, lastName: account.lastName });
      });
    }

    // this.teamForm.valueChanges.subscribe(value=> {
    //   if(this.teamForm?.value?.approver){
    //     const approver = this.teamForm?.value?.approver as IUser[];
    //     approver.forEach(user=> {
    //       this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: 'Approver' })
    //     })
    //   }
    //})
  }

  deleteMem(user: Role, role?: Roles): void {
    if (!role) {
      return;
    }

    const roleProperty = this.getProperty(role);
    this.deleteMember.emit({ user, role: roleProperty });

    const index = this.assignedUsers?.findIndex(x => x.id === user.id);
    if (index && index >= 0) {
      this.assignedUsers?.splice(index, 1);
    }
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
}
