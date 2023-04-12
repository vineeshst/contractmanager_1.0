import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { ConfigTileViewComponent } from './config-tile-view/config-tile-view.component';
import { AddContractTypeComponent } from './add-contract-type/agreement-contract-type/add-contract-type.component';
import { AssociationDialogComponent } from './add-contract-type/agreement-contract-type/association-dialog/association-dialog.component';
import { AttributeDialogComponent } from './add-contract-type/agreement-contract-type/attribute-dialog/attribute-dialog.component';
import { ContractTypesComponent } from './add-contract-type/contract-types/contract-types.component';
import { ViewContractTypesComponent } from './view-contract-types/view-contract-types.component';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material.module';
import { NgJhipsterModule } from 'ng-jhipster';
import { TeamManagementModule } from '../shared/team-management/team-management.module';
import { ApiConfigSettingsComponent } from './oauth-api/api-config-settings/api-config-settings.component';
import { AddApiServiceComponent } from './oauth-api/add-api-service/add-api-service.component';
import { OauthAuthorizeSuccessComponent } from './oauth-api/oauth-authorize-success/oauth-authorize-success.component';
import { AddContractCategoryComponent } from './add-contract-category/add-contract-category.component';

// const route: Routes = [
//   {
//     path: '',
//     component: ConfigTileViewComponent,
//   },
//   {
//     path: 'create-contract-type',
//     component: ContractTypesComponent,
//   },
//   {
//     path: 'create-contract-type/agreement',
//     component: AddContractTypeComponent,
//   },
//   {
//     path: 'create-contract-type/agreement/:contractTypeId',
//     component: AddContractTypeComponent,
//   },
//   {
//     path: 'view-contract-types',
//     component: ViewContractTypesComponent,
//   },
// ];

@NgModule({
  declarations: [
    ConfigTileViewComponent,
    AddContractTypeComponent,
    AssociationDialogComponent,
    AttributeDialogComponent,
    ContractTypesComponent,
    ViewContractTypesComponent,
    ApiConfigSettingsComponent,
    AddApiServiceComponent,
    OauthAuthorizeSuccessComponent,
    AddContractCategoryComponent,
  ],
  imports: [CommonModule, SharedModule, MaterialModule, RouterModule, CdkStepperModule, NgJhipsterModule, TeamManagementModule],
})
export class ConfigurationModule {}
