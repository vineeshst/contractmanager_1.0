export interface IUser {
  id?: any;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated?: boolean;
  langKey?: string;
  authorities?: string[];
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  password?: string;
  externalIdentifier?: string;
  typeOfUser?: string;
  oragnaisationUnit?: string;
  isAdministrator?: boolean;
  isAuthorisedSignatory?: boolean;
  departmentId?: string;
  isSignatoryAgent?: boolean;
  hierarchyLevel?: number;
  userGroup?: {};
  securityGroup?: {};
  expiryDate?: Date;
  supervisorUser?: string;
  preference?: Preference;
}

export class User implements IUser {
  constructor(
    public id?: any,
    public login?: string,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public activated?: boolean,
    public langKey?: string,
    public authorities?: string[],
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public password?: string,
    public externalIdentifier?: string,
    public typeOfUser?: string,
    public organisationUnit?: string,
    public isAdministrator?: boolean,
    public isAuthorisedSignatory?: boolean,
    public departmentId?: string,
    public isSignatoryAgent?: boolean,
    public hierarchyLevel?: number,
    public userGroup?: {},
    public securityGroup?: {},
    public expiryDate?: Date,
    public supervisorUser?: string,
    public preference?: Preference
  ) {}
}

export class Preference {
  recordsPerPage?: number;
  gridChoice?: string;
  recordsLayout?: string;
  dashboardLayout?: string;
  isSearchPanelVisible?: boolean;
  openRecordInSameTab?: boolean;
  language?: string;
  timeZone?: string;
  dateFormat?: string;
  timeFormat?: string;
  preferredCurrency?: string;
}

// export class UserSelection implements IUser {
//   isSelected?: boolean = false;
//   constructor(
//     public id?: string,
//     public login?: string,
//     public firstName?: string | null,
//     public lastName?: string | null,
//     public email?: string,
//     public activated?: boolean,
//     public langKey?: string,
//     public authorities?: string[],
//     public createdBy?: string,
//     public createdDate?: Date,
//     public lastModifiedBy?: string,
//     public lastModifiedDate?: Date
//   ) {}
// }
