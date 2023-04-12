import { SignatoryStatus } from '../shared/constants/contract-manager.constants';

export class Signatory {
  email: string | undefined;
  signStatus: SignatoryStatus | undefined;

  constructor(email: string | undefined) {
    this.email = email;
  }
}
