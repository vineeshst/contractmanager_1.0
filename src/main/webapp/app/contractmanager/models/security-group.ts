import { User } from '../../admin/user-management/user-management.model';
import { ContractType } from './contractType';

export class SecurityGroup {
  public id?: string | null;
  public name!: string;
  public description?: string;
  public kpi?: Privilege[];
  public report?: Privilege[];
  public privilege?: Privilege[];
  public members?: User[];
  public contractTypes?: ContractType[];
  public createdByUser?: string;
  public createdDate?: Date;
}

export class Privilege {
  name!: string | undefined;
  privilege!: Privileges;
}

export enum Privileges {
  NONE,
  VIEW,
  MANAGE,
}
