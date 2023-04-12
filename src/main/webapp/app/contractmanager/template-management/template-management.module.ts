import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TemplateTileViewComponent } from './template-tile-view/template-tile-view.component';
import { AddContractTemplateComponent } from './add-contract-template/add-contract-template.component';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { MaterialModule } from '../../shared/material.module';
import { TeamManagementModule } from '../shared/team-management/team-management.module';
import { ViewContractTemplatesComponent } from './view-contract-templates/view-contract-templates.component';
import { NgxDocViewerModule } from 'ngx-doc-viewer';

@NgModule({
  declarations: [TemplateTileViewComponent, AddContractTemplateComponent, ViewContractTemplatesComponent],
  imports: [
    CommonModule,
    SharedModule,
    MaterialModule,
    ReactiveFormsModule,
    CdkStepperModule,
    RouterModule,
    TeamManagementModule,
    NgxDocViewerModule,
  ],
})
export class TemplateManagementModule {}
