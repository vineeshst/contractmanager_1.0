import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule} from "@angular/router";
import {SharedModule} from "../../shared/shared.module";
import {MaterialModule} from "../../shared/material.module";
import {ReactiveFormsModule} from "@angular/forms";
import {CdkStepperModule} from "@angular/cdk/stepper";
import {TeamManagementModule} from "../shared/team-management/team-management.module";
import {NgxDocViewerModule} from "ngx-doc-viewer";
import { MySettingsComponent } from './my-settings/my-settings.component';



@NgModule({
  declarations: [
    MySettingsComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    MaterialModule,
    ReactiveFormsModule,
    CdkStepperModule,
    RouterModule,
    TeamManagementModule,
    NgxDocViewerModule,
  ]
})
export class SettingsModule { }
