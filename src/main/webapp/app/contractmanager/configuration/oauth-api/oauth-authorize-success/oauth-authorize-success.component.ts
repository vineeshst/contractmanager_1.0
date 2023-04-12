import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ContractmanangerService } from '../../../contractmananger.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../models/apiService';

@Component({
  selector: 'jhi-oauth-authorize-success',
  templateUrl: './oauth-authorize-success.component.html',
  styleUrls: ['./oauth-authorize-success.component.scss'],
})
export class OauthAuthorizeSuccessComponent {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private contractmanangerService: ContractmanangerService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // this.sub = this.route
    //   .paramMap
    //   .subscribe(params => {
    //     // Defaults to 0 if no query param provided.
    //     this.page = +params['page'] || 0;
    //   });
    const apiService: ApiService = new ApiService();
    this.activatedRoute.queryParams.subscribe(params => {
      apiService.authorizationCode = params['code'];
      apiService.serviceName = params['state'];
    });
    this.contractmanangerService.updateAuthorizationStatus(apiService).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line no-console
      console.log(response);
    });
  }

  private showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }
}
