import { Component, Inject, Input, OnInit, Output } from '@angular/core';
import { IUser, User, UserSelection } from '../../../admin/user-management/user-management.model';
import { UserService } from '../../../core/user/user.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'jhi-add-user-dialog',
  templateUrl: './add-user-dialog.component.html',
  styleUrls: ['./add-user-dialog.component.scss'],
})
export class AddUserDialogComponent implements OnInit {
  users?: UserSelection[];
  selectedUsers: UserSelection[] = [];
  userSearchKey = '';

  get selectedUserCount(): number {
    return this.users?.filter(u => u.isSelected === true).length ?? 0;
  }
  /**
   *
   */
  constructor(
    private userService: UserService,
    public dialogRef: MatDialogRef<AddUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserSelection[]
  ) {}

  ngOnInit(): void {
    this.userService.query().subscribe(res => {
      this.users = res.body ?? [];
      this.users = this.users.map(u => (this.data.find(x => x.id === u.id && x.isSelected) ? { ...u, isSelected: true } : u));
    });
  }

  addMembers(): void {
    this.selectedUsers = this.users?.filter(u => u.isSelected === true) ?? [];
    this.dialogRef.close({ isModified: true, selectedUsers: this.selectedUsers });
  }
}
