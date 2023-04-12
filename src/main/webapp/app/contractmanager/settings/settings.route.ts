import {Route} from '@angular/router';
import { RouteSecurityService } from '../../core/auth/route-security.service';
import { Privileges } from '../models/security-group';
import { SecurityEntities } from '../shared/constants/security-entity.constants';
import {MySettingsComponent} from "./my-settings/my-settings.component";



export const SETTINGS_ROUTE: Route = {
  path: 'settings',
  data: {
    privilege: { name: SecurityEntities.ADMIN_TASKS, privilege: Privileges.MANAGE },
  },
  canActivate: [RouteSecurityService],
  children: [
    {
      path: '',
      component: MySettingsComponent,
    },
    // {
    //   path: 'app-settings',
    //   component: AppSettingsComponent,
    // },
    // {
    //   path: 'profile-settings',
    //   component: ProfileSettingsComponent,
    // },
    // {
    //   path: 'auto-delegation',
    //   component: AutoDelegationComponent,
    // },
  //   {
  //     path: 'view-contract-type/agreement/:contractTypeId',
  //     component: AddContractTypeComponent,
  //   },
  // {
  //   path: 'view-contract-types',
  //   component: ViewContractTypesComponent,
  // },
  //   {
  //     path: 'upload-global-attributes',
  //     component: UploadGlobalAttributesComponent,
  //   },
  //   {
  //     path: 'oauth-api-config',
  //     component: ApiConfigSettingsComponent,
  //   }
  ]
};

