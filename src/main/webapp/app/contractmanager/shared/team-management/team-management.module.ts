import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeamManagementComponent } from './team-management.component';
import { ViewTeamComponent } from './view-team.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../../shared/material.module';
import { SharedModule } from '../../../shared/shared.module';

@NgModule({
  declarations: [TeamManagementComponent, ViewTeamComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, SharedModule],
  exports: [TeamManagementComponent, ViewTeamComponent],
})
export class TeamManagementModule {}
