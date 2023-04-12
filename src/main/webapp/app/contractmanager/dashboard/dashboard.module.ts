import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';

import { TileViewComponent } from './tile-view/tile-view.component';
import { SharedModule } from '../../shared/shared.module';
import { MyTasksComponent } from './my-tasks/my-tasks.component';
import { MaterialModule } from '../../shared/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { TeamManagementModule } from '../shared/team-management/team-management.module';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxDocViewerModule } from 'ngx-doc-viewer';
import { AgreementsComponent } from './agreements/agreements.component';

@NgModule({
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
  declarations: [TileViewComponent, MyTasksComponent, AgreementsComponent],
})
export class DashboardModule {}
