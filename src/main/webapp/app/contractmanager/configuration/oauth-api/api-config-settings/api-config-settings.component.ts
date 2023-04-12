import { Component, ViewChild } from '@angular/core';
import { ApiService } from '../../../models/apiService';
import { Observable } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ContractmanangerService } from '../../../contractmananger.service';
import { DomSanitizer } from '@angular/platform-browser';
import { AddApiServiceComponent } from '../add-api-service/add-api-service.component';

@Component({
  selector: 'jhi-api-config-settings',
  templateUrl: './api-config-settings.component.html',
  styleUrls: ['./api-config-settings.component.scss'],
})
export class ApiConfigSettingsComponent {
  apiServices: ApiService[] = [];
  apiServicesObserver: Observable<ApiService[]> | undefined;
  apiServicesDataSource = new MatTableDataSource<any>([]);
  apiServiceColumns: string[] = ['serviceName', 'clientId', 'action'];
  @ViewChild(MatSort, { static: true }) sort: MatSort | undefined;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator | undefined;
  // adobeUrl = 'https://secure.eu2.adobesign.com/public/oauth?\n' +
  //   'redirect_uri=https://tolocalhost.com&\n' +
  //   'response_type=code&\n' +
  //   'client_id=CBJCHBCAABAA-_ws1WRb5PBCxJ5XaJPq990ef3g1S4nB&\n' +
  //   'state=EsignApiService&\n' +
  //   'scope=user_login:self+agreement_send:account+agreement_read:account+agreement_write:account+library_read:account+library_write:account';
  constructor(private dialog: MatDialog, private contractManagerService: ContractmanangerService, private _sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    this.apiServicesObserver = this.contractManagerService.apiServices;
    this.contractManagerService.getApiServices();
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    this.apiServicesObserver.subscribe(apiServices => {
      if (apiServices.length > 0) {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        const tempApiServices: any[] | undefined = [];
        apiServices.forEach(apiService => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.apiServices[apiService.serviceName] = apiService;
          tempApiServices.push({
            serviceName: apiService.serviceName,
            accessTokenEndPoint: apiService.accessTokenEndPoint,
            authorizationEndPoint: apiService.authorizationEndPoint,
            clientId: apiService.clientId,
            // clientSecret: apiService.clientSecret,
            redirectUri: apiService.redirectUri,
            tokenScope: apiService.tokenScope,
            refreshTokenEndPoint: apiService.refreshTokenEndPoint,
            action: '',
          });
        });
        this.apiServicesDataSource = new MatTableDataSource<any>(tempApiServices);
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        this.apiServicesDataSource.sort = this.sort;
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        this.apiServicesDataSource.paginator = this.paginator;
      }
      // eslint-disable-next-line no-console
      console.log(apiServices);
    });
  }

  addAPIService(): void {
    const dialogRef = this.dialog.open(AddApiServiceComponent, {
      width: '800px',
      height: '600px',
      data: {},
    });
  }

  authorizeApiService(rowElement: {
    authorizationEndPoint: string;
    redirectUri: string;
    clientId: string;
    serviceName: string;
    tokenScope: string;
  }): void {
    const authorizationUrl =
      rowElement.authorizationEndPoint +
      '?redirect_uri=' +
      rowElement.redirectUri +
      '&response_type=code&client_id=' +
      rowElement.clientId +
      '&state=' +
      rowElement.serviceName +
      '&scope=' +
      rowElement.tokenScope;
    window.open(authorizationUrl);
    // let safeUrl;
    // let srcUrl;
    // const dialogRef = this.dialog.open(ViewExternalContentComponent, {
    //   width: '800px',
    //   height: '600px',
    //   data: {
    //     // this.srcUrl = this._sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${this.bookmark.youtubeVideoId}`);
    //     srcUrl : this._sanitizer.bypassSecurityTrustResourceUrl(`https://secure.adobesign.com/public/oauth?redirect_uri=https://tolocalhost.com&response_type=code&client_id=CBJCHBCAABAA-_ws1WRb5PBCxJ5XaJPq990ef3g1S4nB&scope=user_login:self+agreement_send:account+agreement_read:account+agreement_sign+agreement_vault:account:account+agreement_retention:account`),
    //   },
    // });
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-function,@typescript-eslint/no-unused-vars,@typescript-eslint/ban-ts-comment
  // @ts-ignore
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  openApiServiceDetailDialog(rowElement, editMode: string): void {}
}
