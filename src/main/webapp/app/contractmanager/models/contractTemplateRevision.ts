import { ContractTemplate } from './contractTemplate';

export class ContractTemplateRevision {
  constructor(public id: string, public version: number, public contractTemplateRevisions: ContractTemplate[]) {}
}
