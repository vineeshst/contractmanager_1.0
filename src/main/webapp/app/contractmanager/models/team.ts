import { IUser } from '../../entities/user/user.model';
import { NotificationConfig } from './notifications-config';
import { UserGroup } from './user-group';

export class Team {
  contractAdmin: IUser[] = [];
  contributor: IUser[] = [];
  deviationApprover: IUser[] = [];
  approver: IUser[] = [];
  observer: IUser[] = [];
  reviewer: IUser[] = [];
  secondaryOwner: IUser[] = [];
  externalReviewer: IUser[] = [];
  externalSignatory: IUser[] = [];
  internalSignatory: IUser[] = [];
  constructor() {
    //do nothing
  }
}
