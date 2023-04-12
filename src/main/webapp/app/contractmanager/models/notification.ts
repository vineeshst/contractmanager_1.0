import { EntityType } from './entity';

export class Notif {
  id!: string;
  type!: NotificationType;
  message!: string;
  read!: boolean;
  date!: Date;
  actionUser!: string;
  entityId?: string;
  entityName?: string;
  entityType!: EntityType;
}

export enum NotificationType {
  SentForApproval = 'Sent For Approval',
  Approved = 'Approved',
  Rejected = 'Rejected',
  Modified = 'Modified',
  Created = 'Created',
  Expired = 'Expired',
  Executed = 'Executed',
}
