import { Roles } from '../shared/constants/user-role.constans';

export class Role {
  firstName: string | undefined;
  lastName: string | undefined;
  role!: Roles;
  id!: string;
  type!: string;
  members = 0;
}
