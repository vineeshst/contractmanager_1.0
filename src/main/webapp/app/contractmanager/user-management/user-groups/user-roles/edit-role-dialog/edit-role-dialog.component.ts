import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserRole } from '../../../../models/user-roles';
import { UserManagementService } from '../../../user-management.service';

@Component({
  selector: 'jhi-edit-role-dialog',
  templateUrl: './edit-role-dialog.component.html',
  styleUrls: ['./edit-role-dialog.component.scss'],
})
export class EditRoleDialogComponent implements OnInit {
  editRoleForm = this.fb.group({
    id: [''],
    name: [{ value: '', disabled: true }],
    displayName: ['', Validators.minLength(3)],
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { role: UserRole },
    private fb: FormBuilder,
    private userManagementService: UserManagementService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.editRoleForm.patchValue({
      id: this.data.role.id,
      name: this.data.role.name,
      displayName: this.data.role.displayName,
    });
  }

  save(): void {
    const userRole: UserRole = Object.assign(new UserRole(), this.editRoleForm.getRawValue());
    this.userManagementService.updateRoleDisplayName(userRole).subscribe(response => {
      this.snackBar.open('User Role is updated successfully', 'Ok', {
        duration: 2000,
      });
    });
  }
}
