import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Association } from '../../../../../contractmanager/models/association';

@Component({
  selector: 'jhi-association-dialog',
  templateUrl: './association-dialog.component.html',
  styleUrls: ['./association-dialog.component.scss'],
})
export class AssociationDialogComponent {
  associationForm = this.fb.group({
    associationName: ['', [Validators.required, Validators.minLength(3)]],
    associatedContractType: [''],
    allowInheritance: [false],
    allowTwowayLinkage: [false],
    allowMutipleInstance: [false],
    allowPeerCreationWizard: [false],
    IsMandatory: [false],
    useCustomNomenclature: [false],
    defineByRule: [false],
    inlineAssociation: [false],
  });

  public get associationName(): FormControl {
    return this.fb.control('associationName');
  }

  public get associatedContractType(): FormControl {
    return this.fb.control('associatedContractType');
  }

  constructor(private fb: FormBuilder, public dialogRef: MatDialogRef<AssociationDialogComponent>) {}

  save(): void {
    const attribute: Association = new Association();
    Object.assign(attribute, this.associationForm.value);
    this.dialogRef.close(attribute);
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
