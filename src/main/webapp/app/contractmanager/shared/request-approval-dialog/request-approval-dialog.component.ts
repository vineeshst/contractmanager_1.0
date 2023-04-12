import { Component, Inject, Input, OnInit, Output } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IUser } from '../../../admin/user-management/user-management.model';

@Component({
  selector: 'jhi-request-approval-dialog',
  templateUrl: './request-approval-dialog.component.html',
  styleUrls: ['./request-approval-dialog.component.scss'],
})
export class RequestApprovalDialogComponent {
  message?: string;

  constructor(@Inject(MAT_DIALOG_DATA) public approvers: IUser[], public dialogRef: MatDialogRef<RequestApprovalDialogComponent>) {}

  cancel(): void {
    this.dialogRef.close(false);
  }
}
