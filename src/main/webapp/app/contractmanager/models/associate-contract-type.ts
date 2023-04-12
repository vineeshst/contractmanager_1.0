import { ContractType } from './contractType';

export class AssociateContractType {
  constructor(public contractType: ContractType, public isMandatory: boolean) {}
}
