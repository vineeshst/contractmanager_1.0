import { IUser } from '../../admin/user-management/user-management.model';
import { Roles } from '../shared/constants/user-role.constans';

export class UserGroup {
  public id?: string;
  public name?: string;
  public description?: string;
  public members: IUser[];
  public created_on?: Date;
  public createdBy?: string;

  constructor() {
    this.members = [];
  }
}

export class UserGroupSelection extends UserGroup {
  selectedRole?: Roles;
}
