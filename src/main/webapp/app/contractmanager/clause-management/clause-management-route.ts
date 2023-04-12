import { Route } from '@angular/router';
import { ClauseTileViewComponent } from './clause-tile-view/clause-tile-view.component';
import { AddContractClauseComponent } from './add-contract-clause/add-contract-clause.component';
import { ViewContractClausesComponent } from './view-contract-clauses/view-contract-clauses.component';

export const CLAUSE_MANAGEMENT_ROUTE: Route = {
  path: 'clause',
  children: [
    {
      path: '',
      component: ClauseTileViewComponent,
    },
    {
      path: 'add-contract-clause',
      component: AddContractClauseComponent,
    },
    {
      path: 'add-contract-clause/:clauseId',
      component: AddContractClauseComponent,
    },
    {
      path: 'view-contract-clauses',
      component: ViewContractClausesComponent,
    },
  ],
};
