import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AddContractAgreementComponent } from './add-contract-agreement/add-contract-agreement.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { ViewContractAgreementsComponent } from './view-contract-agreements/view-contract-agreements.component';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material.module';
import { TeamManagementModule } from '../shared/team-management/team-management.module';
import { AgreementTileViewComponent } from './agreement-tile-view/agreement-tile-view.component';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxDocViewerModule } from 'ngx-doc-viewer';

@NgModule({
  declarations: [AddContractAgreementComponent, ViewContractAgreementsComponent, AgreementTileViewComponent],
  imports: [
    CommonModule,
    SharedModule,
    MaterialModule,
    ReactiveFormsModule,
    CdkStepperModule,
    TeamManagementModule,
    RouterModule,
    PdfViewerModule,
    NgxDocViewerModule,
  ],
})
export class AgreementManagementModule {}
