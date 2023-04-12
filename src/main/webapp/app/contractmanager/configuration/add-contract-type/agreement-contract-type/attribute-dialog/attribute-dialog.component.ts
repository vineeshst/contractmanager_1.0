import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AttributeConfig } from '../../../../models/attributeConfig';

@Component({
  selector: 'jhi-attribute-dialog',
  templateUrl: './attribute-dialog.component.html',
  styleUrls: ['./attribute-dialog.component.scss'],
})
export class AttributeDialogComponent implements OnInit {
  isEditMode = false;

  attributesForm = this.fb.group({
    attributeName: [''],
    displayName: [''],
    pageName: [''],
    dataType: [''],
    group: [''],
    helpMessage: [''],
    source: [''],
    dependsOnAttribute: [''],
    attributeOptions: this.fb.group({
      isGlobal: [false],
      isDefault: [false],
      isEditable: [false],
      isMandatory: [false],
      defaultByRule: [false],
      isInherit: [false],
      tempateCreation: [false],
      isSearchable: [false],
      isConditional: [false],
      isLookup: [false],
      isMultiSelect: [false],
      hasLookupFilter: [false],
      isCasacde: [false],
      justificationRequired: [false],
      isSupersedableByAmendments: [false],
      isSupersedableByAssignments: [false],
      isSupersedableByTermaination: [false],
      isInheritOnAmendments: [false],
      isDependsOnValueByReference: [false],
      enableExpressions: [false],
      isAiDiscoverable: [false],
    }),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { mode: string; attribute: AttributeConfig },
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AttributeDialogComponent>
  ) {}

  ngOnInit(): void {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (!this.data) {
      return;
    }
    if (this.data.mode === 'edit') {
      this.isEditMode = true;
      this.attributesForm.patchValue(Object.assign({}, this.data.attribute));
    }
  }

  save(): void {
    const attribute: AttributeConfig = new AttributeConfig('');
    Object.assign(attribute, this.attributesForm.value);
    this.dialogRef.close(attribute);
  }
}
