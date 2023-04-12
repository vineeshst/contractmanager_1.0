import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ViewContractClausesComponent } from './view-contract-clauses/view-contract-clauses.component';
import { ClauseTileViewComponent } from './clause-tile-view/clause-tile-view.component';
import { AddContractClauseComponent } from './add-contract-clause/add-contract-clause.component';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { RouterModule } from '@angular/router';
import { TeamManagementModule } from '../shared/team-management/team-management.module';
import { NgxDocViewerModule } from 'ngx-doc-viewer';

@NgModule({
  declarations: [ViewContractClausesComponent, ClauseTileViewComponent, AddContractClauseComponent],
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
export class ClauseManagementModule {}
