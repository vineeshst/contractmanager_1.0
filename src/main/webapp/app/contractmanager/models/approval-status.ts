import { ApprovalCriteria } from './approval-criteria';
import { UserApprovalStatus } from './user-spproval-status';

export class ApprovalStatus {
  approvalCriteria?: ApprovalCriteria;
  userApprovalStatus?: UserApprovalStatus[];
}
