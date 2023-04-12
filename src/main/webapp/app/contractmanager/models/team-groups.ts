import { UserGroup } from './user-group';

export class TeamGroups {
  approver: UserGroup[] = [];
  contractAdmin: UserGroup[] = [];
  externalReviewer: UserGroup[] = [];
  observer: UserGroup[] = [];
  secondaryOwner: UserGroup[] = [];
  contributor: UserGroup[] = [];
  externalSignatory: UserGroup[] = [];

  constructor() {
    //do nothing
  }
}
