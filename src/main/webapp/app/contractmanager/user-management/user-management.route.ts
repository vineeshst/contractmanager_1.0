import { Route, Routes } from '@angular/router';
import { UserManagementTileViewComponent } from './user-management-tile-view/user-management-tile-view.component';
import { AddUserComponent } from './users/add-user/add-user.component';
import { SecurityGroupComponent } from './security-group/security-group.component';
import { AddSecurityGroupComponent } from './security-group/add-security-group/add-security-group.component';
import { UserGroupsComponent } from './user-groups/user-groups.component';
import { UserRolesComponent } from './user-groups/user-roles/user-roles.component';
import { ViewSecurityGroupsComponent } from './security-group/view-security-groups/view-security-groups.component';
import { AddUserGroupComponent } from './user-groups/add-user-group/add-user-group.component';
import { ViewUserGroupsComponent } from './user-groups/view-user-groups/view-user-groups.component';
import { UserComponent } from './users/user.component';
import { ViewUserComponent } from './users/view-user/view-user.component';
import { SecurityEntities } from '../shared/constants/security-entity.constants';
import { Privileges } from '../models/security-group';
import { RouteSecurityService } from '../../core/auth/route-security.service';

export const UserManagementRoute: Routes = [
  {
    path: '',
    component: UserManagementTileViewComponent,
  },
  {
    path: 'user',
    component: AddUserComponent,
  },
  {
    path: 'security-group',
    component: SecurityGroupComponent,
  },
  {
    path: 'security-group/add-security-group',
    component: AddSecurityGroupComponent,
  },
  {
    path: 'user-groups',
    component: UserGroupsComponent,
  },
  {
    path: 'user-groups/user-roles',
    component: UserRolesComponent,
  },
];

export const USER_MANAGEMENT_ROUTE: Route = {
  path: 'user-management',
  data: {
    privilege: { name: SecurityEntities.ADMIN_TASKS, privilege: Privileges.MANAGE },
  },
  canActivate: [RouteSecurityService],
  children: [
    {
      path: '',
      component: UserManagementTileViewComponent,
    },
    {
      path: 'user',
      component: UserComponent,
    },
    {
      path: 'user/add-user',
      component: AddUserComponent,
    },
    {
      path: 'user/view-users',
      component: ViewUserComponent,
    },
    {
      path: 'user/view-user/:id',
      component: AddUserComponent,
    },
    {
      path: 'security-group',
      component: SecurityGroupComponent,
    },
    {
      path: 'security-group/add-security-group',
      component: AddSecurityGroupComponent,
    },
    {
      path: 'security-group/view-security-groups',
      component: ViewSecurityGroupsComponent,
    },
    {
      path: 'security-group/view-security-group/:id',
      component: AddSecurityGroupComponent,
    },
    {
      path: 'user-groups',
      component: UserGroupsComponent,
    },
    {
      path: 'user-groups/user-roles',
      component: UserRolesComponent,
    },
    {
      path: 'user-groups/add-user-group',
      component: AddUserGroupComponent,
    },
    {
      path: 'user-groups/view-user-group/:id',
      component: AddUserGroupComponent,
    },
    {
      path: 'user-groups/view-user-groups',
      component: ViewUserGroupsComponent,
    },
  ],
};
