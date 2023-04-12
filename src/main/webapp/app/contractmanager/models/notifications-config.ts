import { KeyValue } from '@angular/common';

export class NotificationConfig {
  userConfig?: NotificationMap;
}

export interface NotificationMap {
  [key: string]: boolean;
}
