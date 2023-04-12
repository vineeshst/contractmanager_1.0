import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserManagementRoute } from './user-management.route';
import { UserManagementTileViewComponent } from './user-management-tile-view/user-management-tile-view.component';
import { AddUserComponent } from './users/add-user/add-user.component';
import { SharedModule } from '../../shared/shared.module';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { SecurityGroupComponent } from './security-group/security-group.component';
import { AddSecurityGroupComponent } from './security-group/add-security-group/add-security-group.component';
import { UserGroupsComponent } from './user-groups/user-groups.component';
import { UserRolesComponent } from './user-groups/user-roles/user-roles.component';
import { EditRoleDialogComponent } from './user-groups/user-roles/edit-role-dialog/edit-role-dialog.component';
import { TeamManagementModule } from '../shared/team-management/team-management.module';
import { ViewSecurityGroupsComponent } from './security-group/view-security-groups/view-security-groups.component';
import { AddUserGroupComponent } from './user-groups/add-user-group/add-user-group.component';
import { ViewUserGroupsComponent } from './user-groups/view-user-groups/view-user-groups.component';
import { ViewUserComponent } from './users/view-user/view-user.component';
import { UserComponent } from './users/user.component';

@NgModule({
  declarations: [
    UserManagementTileViewComponent,
    AddUserComponent,
    SecurityGroupComponent,
    AddSecurityGroupComponent,
    UserGroupsComponent,
    UserRolesComponent,
    EditRoleDialogComponent,
    ViewSecurityGroupsComponent,
    AddUserGroupComponent,
    ViewUserGroupsComponent,
    ViewUserComponent,
    UserComponent,
  ],
  imports: [TeamManagementModule, RouterModule.forChild(UserManagementRoute), CommonModule, SharedModule, CdkStepperModule],
})
export class UserManagementModule {}
