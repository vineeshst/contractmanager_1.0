import { AttributeConfig } from './attributeConfig';
import { AssociateContractType } from './associate-contract-type';
import { ContractCategory } from './contract-category';
import { Association } from './association';
import { Team } from './team';
import { DisplayPreference } from './display-preference';
import { ContractTypeStatus } from '../shared/constants/contract-type.constants';
import { ApprovalStatus } from './approval-status';
import { IEntity } from './ientity';
import { EntityStatus } from '../shared/constants/entity-status';
import { TeamGroups } from './team-groups';

export class ContractType implements IEntity {
  entityName!: string;
  entityRole!: string;
  entityId!: string;
  id: number | undefined;
  contractCategory: ContractCategory | undefined;
  contractType: string | undefined;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  contractTypeCode: string;
  name: string | undefined;
  code: string | undefined;
  description: string | undefined;
  attributes: AttributeConfig[] | undefined;
  displayPreference: DisplayPreference | undefined;
  allowThirdPartyPaper: boolean | undefined;
  allowClauseAssembly: boolean | undefined;
  qrCode: boolean | undefined;
  allowCopyWithAssociations: boolean | undefined;
  twoColumnAttributeLayout: boolean | undefined;
  enableCollaboration: boolean | undefined;
  enableAutoSupersede: boolean | undefined;
  expandDropdownOnMouseHover: boolean | undefined;
  associations: AssociateContractType[] | undefined;
  team: Team | undefined;
  teamGroups?: TeamGroups;
  version: number | undefined;
  updatedOn: Date | undefined;
  createdBy: string | undefined;
  createdDate: Date | undefined;
  status!: EntityStatus;
  history: any;
  approvalStatus?: ApprovalStatus;
}
