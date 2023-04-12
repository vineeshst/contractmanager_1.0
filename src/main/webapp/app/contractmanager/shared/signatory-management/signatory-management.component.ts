import { Component, Inject, Input } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AbstractControl, FormArray, FormBuilder, Validators } from '@angular/forms';
import { ContractmanangerService } from '../../contractmananger.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AddContractAgreementComponent } from '../../agreement-management/add-contract-agreement/add-contract-agreement.component';
import { Recipient } from '../../models/recipient';
import { User } from '../../../admin/user-management/user-management.model';

@Component({
  selector: 'jhi-signatory-management',
  templateUrl: './signatory-management.component.html',
  styleUrls: ['./signatory-management.component.scss'],
})
export class SignatoryManagementComponent {
  recipientsFields: Recipient[] = [];
  recipientsDataSource = new BehaviorSubject<AbstractControl[]>([]);
  // recipientsDetailsDisplayColumns = ['position','email', 'action'];
  recipientsDetailsDisplayColumns = [];
  recipientDetailsRows: FormArray = this.fb.array([]);
  sendForSignatureForm = this.fb.group({
    agreementId: new Object(),
    recipients: this.recipientDetailsRows,
    agreementSignMessage: [''],
    signOrderRequired: [false],
  });
  isSignSendInProgress = false;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  get signOrderRequired(): boolean {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return this.sendForSignatureForm.get('signOrderRequired')?.value;
  }

  @Input() signatories: User[] = [];

  constructor(
    private fb: FormBuilder,
    private contractmanagerService: ContractmanangerService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private router: Router,
    private dialogRef: MatDialogRef<AddContractAgreementComponent>
  ) {}

  ngOnInit(): void {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    this.recipientsDetailsDisplayColumns = ['email', 'action'];
    this.sendForSignatureForm.patchValue({
      agreementId: this.data.selectedAgreementId,
    });

    if (this.data.signatories && this.data.signatories.length > 0) {
      const existing = this.data.signatories as User[];

      existing.forEach(user => {
        if (user.email) {
          this.recipientsFields.push(new Recipient(user.email));
        }
      });
    } else {
      this.recipientsFields = [new Recipient('')];
    }

    this.recipientsFields.forEach((recipient: Recipient) => this.addRow(recipient, false));
  }

  addRow(recipient?: Recipient, noUpdate?: boolean): void {
    const row = this.fb.group({
      email: [recipient?.email ? recipient.email : null, [Validators.required, Validators.email]],
    });
    this.recipientDetailsRows.push(row);
    if (!noUpdate) {
      this.updateView();
    }
  }
  updateView(): void {
    this.recipientsDataSource.next(this.recipientDetailsRows.controls);
  }

  deleteAttributeMetaRow(rowIndex: number): void {
    this.recipientDetailsRows.removeAt(rowIndex);
    this.updateView();
  }

  sendForSignature(): void {
    this.isSignSendInProgress = true;
    this.contractmanagerService.sendAgreementForSignature(this.sendForSignatureForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.isSignSendInProgress = false;

      this.router.navigate(['/contractmanager/agreement/view-contract-agreements']).then(() => {
        window.location.reload();
      });
      this.dialogRef.close();
    });
  }
  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }

  toggleSignOrder(isToggleOn: boolean): void {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    this.recipientsDetailsDisplayColumns = isToggleOn ? ['position', 'email', 'action'] : ['email', 'action'];
  }
}
