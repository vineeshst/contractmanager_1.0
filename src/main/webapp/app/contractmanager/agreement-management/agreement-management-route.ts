import { Route } from '@angular/router';
import { AgreementTileViewComponent } from './agreement-tile-view/agreement-tile-view.component';
import { AddContractAgreementComponent } from './add-contract-agreement/add-contract-agreement.component';
import { ViewContractAgreementsComponent } from './view-contract-agreements/view-contract-agreements.component';
import { SecurityEntities } from '../shared/constants/security-entity.constants';
import { Privileges } from '../models/security-group';
import { RouteSecurityService } from '../../core/auth/route-security.service';

export const AGREEMENT_MANAGEMENT_ROUTE: Route = {
  path: 'agreement',
  children: [
    {
      path: '',
      component: AgreementTileViewComponent,
    },
    {
      path: 'add-contract-agreement',
      data: {
        privilege: { name: SecurityEntities.AGREEMENT, privilege: Privileges.MANAGE },
      },
      canActivate: [RouteSecurityService],
      component: AddContractAgreementComponent,
    },
    {
      path: 'add-contract-agreement/:agreementId',
      component: AddContractAgreementComponent,
      data: {
        privilege: { name: SecurityEntities.AGREEMENT, privilege: Privileges.VIEW },
      },
      canActivate: [RouteSecurityService],
    },
    {
      path: 'view-contract-agreements',
      component: ViewContractAgreementsComponent,
      data: {
        privilege: { name: SecurityEntities.AGREEMENT, privilege: Privileges.VIEW },
      },
      canActivate: [RouteSecurityService],
    },
  ],
};
