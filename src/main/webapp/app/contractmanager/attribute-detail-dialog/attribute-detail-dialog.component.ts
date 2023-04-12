import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { AttributeMetaField } from 'app/contractmanager/models/attributeMetaField';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'jhi-attribute-detail-dialog',
  templateUrl: './attribute-detail-dialog.component.html',
  encapsulation: ViewEncapsulation.ShadowDom,
  styleUrls: ['./attribute-detail-dialog.component.scss'],
})
export class AttributeDetailDialogComponent implements OnInit {
  attributeDetailDataSource = new MatTableDataSource<AttributeMetaField>([]);
  columnsAttributeDetails: string[] = ['key', 'value'];
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}

  ngOnInit(): void {
    const rows = [];
    const selectedAttributeMeta = this.data.selectedAttributeMeta;
    const metaFields = Object.keys(selectedAttributeMeta);
    rows.push(new AttributeMetaField('Attribute Name', this.data.selectedAttrName, false));
    metaFields.forEach(metaField => {
      if (metaField !== 'isAdded') {rows.push(new AttributeMetaField(metaField, selectedAttributeMeta[metaField], false));}
    });
    this.attributeDetailDataSource = new MatTableDataSource<AttributeMetaField>(rows);
  }
}
