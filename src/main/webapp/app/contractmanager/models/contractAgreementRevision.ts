import { ContractAgreement } from './contractAgreement';

export class ContractAgreementRevision {
  constructor(public id: string, public version: number, public contractAgreementRevisions: ContractAgreement[]) {}
}
