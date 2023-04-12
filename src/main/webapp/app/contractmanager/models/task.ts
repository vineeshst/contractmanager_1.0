export class Task {
  type!: TaskType;
  status!: boolean;
  contractTypeId?: string;
  contractAgreementId?: string;
  description?: string;
  createdDate!: Date;
  recordName!: string;
}

export enum TaskType {
  CONTRACT_TYPE_APPROVAL = 'CONTRACT_TYPE_APPROVAL',
  CONTRACT_AGREEMENT_APPROVAL = 'CONTRACT_AGREEMENT_APPROVAL',
  TEMPLATE_APPROVAL = 'TEMPLATE_APPROVAL',
  CLAUSE_APPROVAL = 'CLAUSE_APPROVAL',
  CONTRACT_AGREEMENT_SIGNATURE = 'CONTRACT_AGREEMENT_SIGNATURE',
}
