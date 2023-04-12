import { Route } from '@angular/router';
import { UploadAttributeMetaComponent } from './upload-attribute-meta.component';

export const uploadAttributeMetaRoute: Route = {
  path: 'upload-attribute-meta',
  component: UploadAttributeMetaComponent,
  data: {
    authorities: [],
    pageTitle: 'register.title',
  },
};
