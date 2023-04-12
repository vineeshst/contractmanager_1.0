import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function numberValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) {
      return null;
    }

    // const hasUpperCase = /[A-Z]+/.test(value);
    //
    // const hasLowerCase = /[a-z]+/.test(value);
    //
    // const hasNumeric = /[0-9]+/.test(value);

    const numberValid = /^-?(0|[1-9]\d*)?$/.test(value);

    return !numberValid ? { number: true } : null;
  };
}
