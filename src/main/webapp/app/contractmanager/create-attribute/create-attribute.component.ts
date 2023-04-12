import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { AttributeMetaField } from 'app/contractmanager/models/attributeMetaField';
import { BehaviorSubject } from 'rxjs';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'jhi-create-attribute',
  templateUrl: './create-attribute.component.html',
  encapsulation: ViewEncapsulation.ShadowDom,
  styleUrls: ['./create-attribute.component.scss'],
})
export class CreateAttributeComponent implements OnInit {
  attributeMetaDetailFields: AttributeMetaField[] = [];
  attributeMetaDetailsDataSource = new BehaviorSubject<AbstractControl[]>([]);
  attributeMetaDetailsDisplayColumns = ['key', 'value', 'action'];
  attributeMetaDetailsRows: FormArray = this.fb.array([]);
  attributeMetaDetailsFormGroup: FormGroup = this.fb.group({ attributeMetaDetailsFormArray: this.attributeMetaDetailsRows });
  constructor(private fb: FormBuilder, @Inject(MAT_DIALOG_DATA) public data: any) {}

  ngOnInit(): void {
    if (this.data.editAttributeMetaData !== null) {
      const editAttributeKeys = Object.keys(this.data.editAttributeMetaData);
      this.attributeMetaDetailFields = [new AttributeMetaField('Attribute Name', this.data.editAttributeName, false)];
      editAttributeKeys.forEach(attributeKey => {
        this.attributeMetaDetailFields.push(new AttributeMetaField(attributeKey, this.data.editAttributeMetaData[attributeKey], false));
      });
    } else {
      this.attributeMetaDetailFields = [new AttributeMetaField('Attribute Name', '', false)];
    }
    this.attributeMetaDetailFields.forEach((attributeMetaField: AttributeMetaField) => this.addRow(attributeMetaField, false));
    this.updateView();
  }
  emptyTable(): void {
    while (this.attributeMetaDetailsRows.length !== 0) {
      this.attributeMetaDetailsRows.removeAt(0);
    }
  }

  addRow(attributeMetaField?: AttributeMetaField, noUpdate?: boolean): void {
    const row = this.fb.group({
      key: [attributeMetaField?.key ? attributeMetaField.key : null, []],
      value: [attributeMetaField?.value ? attributeMetaField.value : null, []],
    });
    this.attributeMetaDetailsRows.push(row);
    if (!noUpdate) {
      this.updateView();
    }
  }

  updateView(): void {
    this.attributeMetaDetailsDataSource.next(this.attributeMetaDetailsRows.controls);
  }

  deleteAttributeMetaRow(rowIndex: number): void {
    this.attributeMetaDetailsRows.removeAt(rowIndex);
    this.updateView();
  }
}
