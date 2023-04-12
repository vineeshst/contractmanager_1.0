import { Route } from '@angular/router';

import { HeaderComponent } from '../header/header.component';

export const headerRoute: Route = {
  path: '',
  component: HeaderComponent,
  outlet: 'header',
};
