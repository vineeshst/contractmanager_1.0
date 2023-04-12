import { IUser } from '../../admin/user-management/user-management.model';
import { ContractType } from './contractType';
import { Team } from './team';
import { ContractCategory } from './contract-category';
import { ApprovalStatus } from './approval-status';
import { IEntity } from './ientity';

export class ContractTemplate implements IEntity {
  entityName!: string;
  entityRole!: string;
  entityId!: string;
  id: number | undefined;
  templateName: string | undefined;
  code: string | undefined;
  contractCategory: ContractCategory | undefined;
  contractType: ContractType | undefined;
  filePath: string | undefined;
  language: string | undefined;
  secondaryLanguage: string | undefined;
  dateFormat: string | undefined;
  effectiveFrom: Date | undefined;
  effectiveTo: Date | undefined;
  status: string | undefined;
  isPrimary: boolean | undefined;
  description: string | undefined;
  clauseNames: string[] | undefined;
  version: number | undefined;
  team: Team | undefined;
  updatedOn: Date | undefined;
  createdBy: string | undefined;
  createdDate: Date | undefined;
  tempFileName: string | undefined;
  history: any;
  approvalStatus?: ApprovalStatus;
}
