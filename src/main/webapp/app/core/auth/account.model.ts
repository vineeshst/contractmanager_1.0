import { SecurityGroup } from '../../contractmanager/models/security-group';

export class Account {
  constructor(
    public activated: boolean,
    public authorities: string[],
    public securityGroup: SecurityGroup | null,
    public email: string,
    public firstName: string | null,
    public langKey: string,
    public lastName: string | null,
    public login: string,
    public imageUrl: string | null
  ) {}
}
