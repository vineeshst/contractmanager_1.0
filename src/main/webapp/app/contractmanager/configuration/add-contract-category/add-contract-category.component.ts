import { Component, ViewEncapsulation } from '@angular/core';
import { AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ContractmanangerService } from '../../contractmananger.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'jhi-add-contract-category',
  templateUrl: './add-contract-category.component.html',
  // encapsulation: ViewEncapsulation.ShadowDom,
  styleUrls: ['./add-contract-category.component.scss'],
})
export class AddContractCategoryComponent {
  contractCategoryFrom = this.fb.group({
    categoryName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    categoryDescription: ['', Validators.maxLength(1000)],
  });
  get categoryName(): AbstractControl {
    return <AbstractControl>this.contractCategoryFrom.get('categoryName');
  }
  get description(): AbstractControl {
    return <AbstractControl>this.contractCategoryFrom.get('categoryDescription');
  }
  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AddContractCategoryComponent>,
    private contractmanangerService: ContractmanangerService,
    private snackBar: MatSnackBar
  ) {}

  save(): void {
    this.contractmanangerService.addContractCategory(this.contractCategoryFrom.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line no-console
      console.log(response);
      this.dialogRef.close();
    });
  }
  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }
}
