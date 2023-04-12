import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RuleTileViewComponent } from './rule-tile-view/rule-tile-view.component';
import { AddContractRuleComponent } from './add-contract-rule/add-contract-rule.component';
import { ViewContractRulesComponent } from './view-contract-rules/view-contract-rules.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../../shared/shared.module';
import { MaterialModule } from '../../../shared/material.module';
import { CdkStepperModule } from '@angular/cdk/stepper';

@NgModule({
  declarations: [RuleTileViewComponent, AddContractRuleComponent, ViewContractRulesComponent],
  imports: [CommonModule, RouterModule, ReactiveFormsModule, SharedModule, MaterialModule, CdkStepperModule],
})
export class RuleManagementModule {}
