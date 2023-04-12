import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { EditRoleDialogComponent } from './edit-role-dialog/edit-role-dialog.component';
import { UserManagementService } from '../../user-management.service';
import { UserRole } from '../../../models/user-roles';

@Component({
  selector: 'jhi-user-roles',
  templateUrl: './user-roles.component.html',
  styleUrls: ['./user-roles.component.scss'],
})
export class UserRolesComponent implements OnInit {
  userRolesDataSource = new MatTableDataSource<UserRole>([]);
  userRolesColumns: string[] = ['roles', 'displayName', 'action'];

  userRoles: UserRole[] = [];

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  constructor(private dialog: MatDialog, private userManagementService: UserManagementService) {}

  ngOnInit(): void {
    this.userManagementService.getMetaUserRoles().subscribe((userRole: UserRole[]) => {
      this.userRoles = userRole;
      this.userRolesDataSource = new MatTableDataSource<UserRole>(this.userRoles);
    });
  }

  editUserRole(role: string): void {
    this.dialog
      .open(EditRoleDialogComponent, {
        data: {
          role,
        },
      })
      .afterClosed()
      .subscribe(() => {
        this.ngOnInit();
      });
  }
}
