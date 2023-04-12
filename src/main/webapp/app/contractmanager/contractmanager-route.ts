import { Route, Routes } from '@angular/router';
import { uploadAttributeMetaRoute } from 'app/contractmanager/upload-attribute-meta/upload-attribute-meta-route';
import { HOME_ROUTE } from './home/home.route';
import { CONFIG_ROUTE } from './configuration/configuration.route';

const CONTRACTMANAGER_ROUTES = [
  uploadAttributeMetaRoute,
  // HOME_ROUTE,
  // CONFIG_ROUTE
];

export const contractmanagerState: Routes = [
  {
    path: '',
    children: CONTRACTMANAGER_ROUTES,
  },
];
