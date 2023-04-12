import { Route } from '@angular/router';

import { ConfigTileViewComponent } from './config-tile-view/config-tile-view.component';
import { ViewContractTypesComponent } from './view-contract-types/view-contract-types.component';
import { ContractTypesComponent } from './add-contract-type/contract-types/contract-types.component';
import { AddContractTypeComponent } from './add-contract-type/agreement-contract-type/add-contract-type.component';
import { RULE_MANAGEMENT_ROUTE } from './rule-management/rule-management-route';
import { ApiConfigSettingsComponent } from './oauth-api/api-config-settings/api-config-settings.component';
// import {AddContractRuleComponent} from "./add-contract-rule/add-contract-rule.component";
// import {ViewContractRulesComponent} from "./view-contract-rules/view-contract-rules.component";
import { RouteSecurityService } from '../../core/auth/route-security.service';
import { Privileges } from '../models/security-group';
import { SecurityEntities } from '../shared/constants/security-entity.constants';

export const CONFIG_ROUTE: Route = {
  path: 'configuration',
  data: {
    privilege: { name: SecurityEntities.ADMIN_TASKS, privilege: Privileges.MANAGE },
  },
  canActivate: [RouteSecurityService],
  children: [
    {
      path: '',
      component: ConfigTileViewComponent,
    },
    {
      path: 'create-contract-type',
      component: ContractTypesComponent,
    },
    {
      path: 'create-contract-type/agreement',
      component: AddContractTypeComponent,
    },
    {
      path: 'view-contract-type/agreement/:contractTypeId',
      component: AddContractTypeComponent,
    },
    {
      path: 'view-contract-types',
      component: ViewContractTypesComponent,
    },
    {
      path: 'oauth-api-config',
      component: ApiConfigSettingsComponent,
    },
    RULE_MANAGEMENT_ROUTE,
    // {
    //   path: 'create-contract-rule',
    //   component: AddContractRuleComponent,
    // },
    // {
    //   path: 'view-contract-rules',
    //   component: ViewContractRulesComponent,
    // },
  ],
};
