import { Route } from '@angular/router';
import { RuleTileViewComponent } from './rule-tile-view/rule-tile-view.component';
import { AddContractRuleComponent } from './add-contract-rule/add-contract-rule.component';
import { ViewContractRulesComponent } from './view-contract-rules/view-contract-rules.component';

export const RULE_MANAGEMENT_ROUTE: Route = {
  path: 'rule',
  children: [
    {
      path: '',
      component: RuleTileViewComponent,
    },
    {
      path: 'add-contract-rule',
      component: AddContractRuleComponent,
    },
    {
      path: 'add-contract-rule/:ruleId',
      component: AddContractRuleComponent,
    },
    {
      path: 'view-contract-rules',
      component: ViewContractRulesComponent,
    },
  ],
};
