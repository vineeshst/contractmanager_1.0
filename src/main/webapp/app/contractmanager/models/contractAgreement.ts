import { AttributeConfig } from './attributeConfig';
import { AssociateContractType } from './associate-contract-type';
import { ContractCategory } from './contract-category';
import { Attribute } from './attribute';
import { ContractType } from './contractType';
import { ContractTemplate } from './contractTemplate';
import { ApprovalStatus } from './approval-status';
import { IEntity } from './ientity';
import { Team } from './team';
import { NotificationConfig } from './notifications-config';
import { TeamGroups } from './team-groups';

export class ContractAgreement implements IEntity {
  entityName!: string;
  entityRole!: string;
  entityId!: string;
  id: number | undefined;
  contractCategory: ContractCategory | undefined;
  contractType: ContractType | undefined;
  agreementName: string | undefined;
  agreementDescription: string | undefined;
  agreementCode: string | undefined;
  attributes: Attribute[] = [];
  template: ContractTemplate | undefined;
  version: number | undefined;
  updatedOn: Date | undefined;
  createdBy: string | undefined;
  createdDate: Date | undefined;
  agreementStatus: string | undefined;
  history: any;
  tempAgreementFile: string | undefined;
  tempTemplateFile: string | undefined;
  approvalStatus?: ApprovalStatus;
  team!: Team;
  teamGroups?: TeamGroups;
  notificationConfig?: NotificationConfig;
}
