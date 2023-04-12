import { Route } from '@angular/router';

import {TileViewComponent} from "./tile-view.component";

export const tileViewRoute: Route = {
  path: 'tile-view',
  component: TileViewComponent,
  data: {
    pageTitle: 'tile-view.title',
  },
};
