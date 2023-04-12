import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'jhi-approve-dialog',
  templateUrl: './approve-dialog.component.html',
  styleUrls: ['./approve-dialog.component.scss'],
})
export class ApproveDialogComponent {
  message?: string = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { isApprovalDialog: boolean },
    public dialogRef: MatDialogRef<ApproveDialogComponent>
  ) {}

  cancel(): void {
    this.dialogRef.close(false);
  }
}
