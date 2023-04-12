import { NgModule } from '@angular/core';

import { SharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { TranslateDirective } from './language/translate.directive';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { DurationPipe } from './date/duration.pipe';
import { FormatMediumDatetimePipe } from './date/format-medium-datetime.pipe';
import { FormatMediumDatePipe } from './date/format-medium-date.pipe';
import { SortByDirective } from './sort/sort-by.directive';
import { SortDirective } from './sort/sort.directive';
import { ItemCountComponent } from './pagination/item-count.component';
import { StepperComponent } from './stepper/stepper.component';
import { AttributeFilterPipe } from './pipe/attribute-filter.pipe';
import { MaterialModule } from './material.module';
import { HasPrivilegeDirective } from './auth/has-privilege.directive';
import { ReactiveFormsModule } from '@angular/forms';
import { UserFilterPipe } from './pipe/user-filter.pipe';
import { UserGroupFilterPipe } from './pipe/user-group-filter.pipe';
import { AgreementTableComponent } from '../contractmanager/shared/agreement-table/agreement-table.component';
import { RouterModule } from '@angular/router';
import { AgreementFilterPipe } from './pipe/agreement-filter.pipe';

@NgModule({
  imports: [SharedLibsModule, MaterialModule, RouterModule],
  declarations: [
    FindLanguageFromKeyPipe,
    TranslateDirective,
    AlertComponent,
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
    StepperComponent,
    AttributeFilterPipe,
    HasPrivilegeDirective,
    UserFilterPipe,
    UserGroupFilterPipe,
    AgreementTableComponent,
    AgreementFilterPipe,
  ],
  exports: [
    SharedLibsModule,
    FindLanguageFromKeyPipe,
    TranslateDirective,
    AlertComponent,
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
    StepperComponent,
    AttributeFilterPipe,
    MaterialModule,
    ReactiveFormsModule,
    HasPrivilegeDirective,
    UserFilterPipe,
    UserGroupFilterPipe,
    AgreementTableComponent,
    AgreementFilterPipe,
  ],
})
export class SharedModule {}
