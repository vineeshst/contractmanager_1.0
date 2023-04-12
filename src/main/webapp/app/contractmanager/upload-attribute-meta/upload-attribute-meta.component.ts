import { Component, ViewEncapsulation } from '@angular/core';
import { ContractmanangerService } from 'app/contractmanager/contractmananger.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'jhi-upload-contract-meta',
  templateUrl: './upload-attribute-meta.component.html',
  // encapsulation: ViewEncapsulation.ShadowDom,
  styleUrls: ['./upload-attribute-meta.component.scss'],
})
export class UploadAttributeMetaComponent {
  constructor(private snackBar: MatSnackBar) {}


  attributeMetaUploaded($event: string): void {
    this.showConfirmationMessage('Uploaded attributes meta successfully');
  }
  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }
}
