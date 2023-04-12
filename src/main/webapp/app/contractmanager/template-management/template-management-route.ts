import { Route, Routes } from '@angular/router';
import { TemplateTileViewComponent } from './template-tile-view/template-tile-view.component';
import { AddContractTemplateComponent } from './add-contract-template/add-contract-template.component';
import { ViewContractTemplatesComponent } from './view-contract-templates/view-contract-templates.component';
import { SecurityEntities } from '../shared/constants/security-entity.constants';
import { Privileges } from '../models/security-group';
import { RouteSecurityService } from '../../core/auth/route-security.service';

export const TEMPLATE_MANAGEMENT_ROUTE: Route = {
  path: 'template',
  children: [
    {
      path: '',
      component: TemplateTileViewComponent,
      data: {
        privilege: { name: SecurityEntities.TEMPLATES, privilege: Privileges.VIEW },
      },
      canActivate: [RouteSecurityService],
    },
    {
      path: 'add-contract-template',
      component: AddContractTemplateComponent,
      data: {
        privilege: { name: SecurityEntities.TEMPLATES, privilege: Privileges.MANAGE },
      },
      canActivate: [RouteSecurityService],
    },
    {
      path: 'add-contract-template/:templateId',
      component: AddContractTemplateComponent,
      data: {
        privilege: { name: SecurityEntities.TEMPLATES, privilege: Privileges.VIEW },
      },
      canActivate: [RouteSecurityService],
    },
    {
      path: 'view-contract-templates',
      component: ViewContractTemplatesComponent,
      data: {
        privilege: { name: SecurityEntities.TEMPLATES, privilege: Privileges.VIEW },
      },
      canActivate: [RouteSecurityService],
    },
  ],
};
