import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { AvatarModule } from 'ngx-avatar';
import { MaterialModule } from '../../shared/material.module';
import { LayoutsModule } from '../layouts/layouts.module';
import { ConfigurationModule } from '../configuration/configuration.module';
import { AgreementManagementModule } from '../agreement-management/agreement-management.module';
import { TemplateManagementModule } from '../template-management/template-management.module';
import { UserManagementModule } from '../user-management/user-management.module';
import { RuleManagementModule } from '../configuration/rule-management/rule-management.module';
import { ClauseManagementModule } from '../clause-management/clause-management.module';
import {SettingsModule} from "../settings/settings.module";

@NgModule({
  imports: [
    SharedModule,
    MaterialModule,
    AvatarModule,
    LayoutsModule,
    ConfigurationModule,
    AgreementManagementModule,
    TemplateManagementModule,
    ClauseManagementModule,
    RuleManagementModule,
    UserManagementModule,
    SettingsModule,
    RouterModule.forChild([HOME_ROUTE]),
  ],
  declarations: [HomeComponent],
  // bootstrap: [HomeComponent],
})
export class HomeModule {}
