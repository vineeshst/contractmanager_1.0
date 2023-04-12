// import { Routes } from '@angular/router';
//
// import {tileViewRoute} from "./tile-view/tile-view.route";
//
// const DASHBOARD_ROUTES = [tileViewRoute];
//
// export const dashboardState: Routes = [
//   {
//     path: '',
//     children: DASHBOARD_ROUTES,
//   },
// ];

import { Route } from '@angular/router';
import { TileViewComponent } from './tile-view/tile-view.component';
import { MyTasksComponent } from './my-tasks/my-tasks.component';
import { AgreementsComponent } from './agreements/agreements.component';

export const DASHBOARD_ROUTE: Route = {
  path: 'dashboard',
  children: [
    {
      path: '',
      component: TileViewComponent,
    },
    {
      path: 'my-tasks',
      component: MyTasksComponent,
    },
    {
      path: 'agreements',
      component: AgreementsComponent,
    },
  ],
};
