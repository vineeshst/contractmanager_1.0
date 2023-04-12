import { CdkStep, CdkStepper } from '@angular/cdk/stepper';
import { Component, forwardRef, Input } from '@angular/core';

@Component({
  selector: 'jhi-cm-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.scss'],
  // eslint-disable-next-line @typescript-eslint/no-use-before-define
  providers: [{ provide: CdkStepper, useExisting: StepperComponent }],
})
export class StepperComponent extends CdkStepper {
  @Input()
  activeClass = 'active';

  getNavigationClass(step: CdkStep): string {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition,no-empty
    if (step.stepControl != null) {
      return step.stepControl.status !== 'INVALID' && step.completed && this.linear ? 'completed' : '';
    }else{
      return '';
    }

  }
}
