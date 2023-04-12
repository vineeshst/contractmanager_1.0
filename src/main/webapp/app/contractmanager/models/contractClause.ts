import { ContractType } from './contractType';
import { Team } from './team';
import { ContractCategory } from './contract-category';
import { ApprovalStatus } from './approval-status';
import { IEntity } from './ientity';

export class ContractClause implements IEntity {
  entityRole!: string;
  entityId!: string;
  entityName!: string;
  id: number | undefined;
  clauseName: string | undefined;
  code: string | undefined;
  contractCategory: ContractCategory | undefined;
  contractType: ContractType | undefined;
  filePath: string | undefined;
  language: string | undefined;
  status: string | undefined;
  description: string | undefined;
  isEditable: boolean | undefined;
  isDeviationAnalysis: boolean | undefined;
  isDependentClause: boolean | undefined;
  version: number | undefined;
  team: Team | undefined;
  updatedOn: Date | undefined;
  createdBy: string | undefined;
  createdDate: Date | undefined;
  tempFileName: string | undefined;
  history: any;
  approvalStatus?: ApprovalStatus;
}
