import { Route, Routes } from '@angular/router';

import { HomeComponent } from './home.component';

import { TileViewComponent } from '../dashboard/tile-view/tile-view.component';
import { ConfigTileViewComponent } from '../configuration/config-tile-view/config-tile-view.component';
import { HeaderComponent } from '../layouts/header/header.component';
import { SidebarComponent } from '../layouts/sidebar/sidebar.component';
import { CONFIG_ROUTE } from '../configuration/configuration.route';
import { AGREEMENT_MANAGEMENT_ROUTE } from '../agreement-management/agreement-management-route';
import { TEMPLATE_MANAGEMENT_ROUTE } from '../template-management/template-management-route';
import { USER_MANAGEMENT_ROUTE } from '../user-management/user-management.route';
import { DASHBOARD_ROUTE } from '../dashboard/dashboard.route';
import { CLAUSE_MANAGEMENT_ROUTE } from '../clause-management/clause-management-route';
import {SETTINGS_ROUTE} from "../settings/settings.route";
// import {tileViewRoute} from "../dashboard/tile-view/tile-view.route";
// const HOME_ROUTES = [tileViewRoute];
export const HOME_ROUTE: Route = {
  path: '',
  component: HomeComponent,
  data: {
    pageTitle: 'home.title',
  },
  children: [
    // {
    //   path: '',
    //   component: SidebarComponent,
    //   outlet: 'sidebar',
    // },
    // {
    //   path: '',
    //   component: HeaderComponent,
    //   outlet: 'header',
    // },
    { path: '', component: TileViewComponent },
    // { path: 'configuration',  redirectTo: 'configuration', outlet: 'content' },
    DASHBOARD_ROUTE,
    CONFIG_ROUTE,
    AGREEMENT_MANAGEMENT_ROUTE,
    TEMPLATE_MANAGEMENT_ROUTE,
    CLAUSE_MANAGEMENT_ROUTE,
    USER_MANAGEMENT_ROUTE,
    SETTINGS_ROUTE
    // { path: 'configuration', component:  ConfigTileViewComponent, outlet: 'content' },

    // {
    //   path: 'configuration',
    //   component: ConfigTileViewComponent,
    //   outlet: 'content',
    // },
  ],
};
// export const homeState: Routes = [
//   {
//     path: 'home1',
//     children: HOME_ROUTES,
//     outlet: 'route1'
//   },
//   // {
//   //   path: 'home1',
//   //   component: tileViewRoute
//   //   outlet: 'route1'
//   // },
//   {
//     path: 'contractmanager',
//     component: HomeComponent,
//     data: {
//       pageTitle: 'home.title',
//     },
//   }
// ];
