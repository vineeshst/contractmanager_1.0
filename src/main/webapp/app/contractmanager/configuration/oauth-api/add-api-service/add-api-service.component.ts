import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { ContractmanangerService } from '../../../contractmananger.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'jhi-add-api-service',
  templateUrl: './add-api-service.component.html',
  styleUrls: ['./add-api-service.component.scss'],
})
export class AddApiServiceComponent {
  apiServiceForm = this.fb.group({
    serviceName: ['', [Validators.required, Validators.minLength(3)]],
    authorizationEndPoint: ['', [Validators.required]],
    refreshTokenEndPoint: ['', [Validators.required]],
    accessTokenEndPoint: ['', [Validators.required]],
    clientId: ['', [Validators.required]],
    clientSecret: ['', [Validators.required]],
    redirectUri: ['', [Validators.required]],
    tokenScope: [''],
    implicitAuthorization: [false],
    userId: ['', []],
    password: ['', []],
  });

  // eslint-disable-next-line @typescript-eslint/no-unsafe-return,@typescript-eslint/ban-ts-comment
  get implicitAuthorization(): boolean {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return this.apiServiceForm.get('implicitAuthorization')?.value;
  }
  get serviceName(): AbstractControl {
    return <AbstractControl>this.apiServiceForm.get('serviceName');
  }
  constructor(private fb: FormBuilder, private contractmanangerService: ContractmanangerService, private snackBar: MatSnackBar) {}

  addApiService(): void {
    this.contractmanangerService.addApiService(this.apiServiceForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line no-console
      console.log(response);
    });
  }
  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }
}
