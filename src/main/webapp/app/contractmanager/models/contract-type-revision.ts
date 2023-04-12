import { ContractType } from './contractType';

export class ContractTypeRevision {
  constructor(public id: string, public version: number, public name: string, public contractTypeRevisions: ContractType[]) {}
}
