import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { contractmanagerState } from './contractmanager-route';
import { MaterialModule } from '../shared/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ContractmanangerService } from './contractmananger.service';
import { CommonModule } from '@angular/common';
import { UploadAttributeMetaComponent } from './upload-attribute-meta/upload-attribute-meta.component';
import { CreateAttributeComponent } from './create-attribute/create-attribute.component';
import { AttributeDetailDialogComponent } from './attribute-detail-dialog/attribute-detail-dialog.component';
// import { TeamManagementComponent } from './shared/team-management/team-management.component';
import { MaterialFileUploadComponent } from '../shared/material-file-upload/material-file-upload.component';
// import { ViewTeamComponent } from './shared/team-management/view-team.component';
import { HomeModule } from './home/home.module';
// import {SidebarComponent} from "../layouts/sidebar/sidebar.component";
// import {HeaderComponent} from "../layouts/header/header.component";
import { AvatarModule } from 'ngx-avatar';
import { DashboardModule } from './dashboard/dashboard.module';
import { ConfigurationModule } from './configuration/configuration.module';
import { TeamManagementModule } from './shared/team-management/team-management.module';
import { SharedLibsModule } from '../shared/shared-libs.module';
import { SignatoryManagementComponent } from './shared/signatory-management/signatory-management.component';
import { SharedModule } from '../shared/shared.module';
import { AddUserDialogComponent } from './shared/add-user-dialog/add-user-dialog.component';
import { RequestApprovalDialogComponent } from './shared/request-approval-dialog/request-approval-dialog.component';
import { ApproveDialogComponent } from './shared/approve-dialog/approve-dialog.component';
import { AgreementTableComponent } from './shared/agreement-table/agreement-table.component';
import { ConfirmationDialogComponent } from './shared/confirmation-dialog/confirmation-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    HomeModule,
    ConfigurationModule,
    DashboardModule,
    TeamManagementModule,
    ReactiveFormsModule,
    RouterModule.forChild(contractmanagerState),
    SharedLibsModule,
    SharedModule,
  ],
  declarations: [
    UploadAttributeMetaComponent,
    CreateAttributeComponent,
    AttributeDetailDialogComponent,
    SignatoryManagementComponent,
    AddUserDialogComponent,
    RequestApprovalDialogComponent,
    ApproveDialogComponent,
    ConfirmationDialogComponent,
    // AgreementTableComponent,
  ],
  providers: [ContractmanangerService],
  exports: [TeamManagementModule],
})
export class ContractmanagerModule {}
