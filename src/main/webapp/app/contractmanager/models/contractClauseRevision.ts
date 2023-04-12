import { ContractClause } from './contractClause';

export class ContractClauseRevision {
  constructor(public id: string, public version: number, public contractClauseRevisions: ContractClause[]) {}
}
