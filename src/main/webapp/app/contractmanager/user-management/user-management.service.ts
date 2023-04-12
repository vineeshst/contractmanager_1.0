import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { IUser, User } from '../../admin/user-management/user-management.model';

import { SERVER_API_URL } from '../../app.constants';
import { SecurityGroup } from '../models/security-group';
import { UserGroup } from '../models/user-group';
import { UserRole } from '../models/user-roles';

@Injectable({
  providedIn: 'root',
})
export class UserManagementService {
  public resourceUrl = SERVER_API_URL + 'api/user-management';

  constructor(private http: HttpClient) {}

  createSecurityGroup(securityGroup: SecurityGroup): Observable<SecurityGroup> {
    const createSecurityGroup = '/security-group/create';
    return this.http.post<SecurityGroup>(`${this.resourceUrl}${createSecurityGroup}`, securityGroup);
  }

  createUserGroup(userGroup: UserGroup): Observable<UserGroup> {
    const updateUserGroup = '/user-group/create';
    return this.http.post<UserGroup>(`${this.resourceUrl}${updateUserGroup}`, userGroup);
  }

  updateSecurityGroup(securityGroup: SecurityGroup): Observable<any> {
    const updateSecurityGroup = '/security-group/update';
    return this.http.put(`${this.resourceUrl}${updateSecurityGroup}`, securityGroup);
  }

  updateRoleDisplayName(userRole: UserRole): Observable<any> {
    const updateMetaRoles = '/user-group/update-meta-roles';
    return this.http.put<UserRole[]>(`${this.resourceUrl}${updateMetaRoles}`, userRole);
  }

  updateUserGroup(userGroupId: string, userGroup: UserGroup): Observable<any> {
    const updateUserGroup = '/user-group/update-user-group';
    return this.http.put<UserRole[]>(`${this.resourceUrl}${updateUserGroup}/${userGroupId}`, userGroup);
  }

  getSecurityGroups(): Observable<SecurityGroup[]> {
    const getSecurityGroups = '/security-group/get-security-groups';
    return this.http.get<SecurityGroup[]>(`${this.resourceUrl}${getSecurityGroups}`);
  }

  getSecurityGroup(securityGroupId: string): Observable<SecurityGroup> {
    const getSecurityGroups = '/security-group/get-security-group';
    return this.http.get<SecurityGroup>(`${this.resourceUrl}${getSecurityGroups}/${securityGroupId}`);
  }

  getMetaPrivileges(): Observable<SecurityGroup> {
    const getMetaPrivileges = '/security-group/get-meta-privileges';
    return this.http.get<SecurityGroup>(`${this.resourceUrl}${getMetaPrivileges}`);
  }

  getMetaUserRoles(): Observable<UserRole[]> {
    const getMetaRoles = '/user-group/get-meta-roles';
    return this.http.get<UserRole[]>(`${this.resourceUrl}${getMetaRoles}`);
  }

  getUserGroup(userGroupId: string): Observable<UserGroup> {
    const getUserGroup = '/user-group/get-user-group';
    return this.http.get<UserGroup>(`${this.resourceUrl}${getUserGroup}/${userGroupId}`);
  }

  getUserGroups(): Observable<UserGroup[]> {
    const getUserGroup = '/user-group/get-user-groups';
    return this.http.get<UserGroup[]>(`${this.resourceUrl}${getUserGroup}`);
  }

  getUser(id: string): Observable<IUser[]> {
    const getUser = '/user';
    return this.http.get<UserGroup[]>(`${this.resourceUrl}${getUser}/${id}`);
  }

  deleteUserGroup(id: string): Observable<any> {
    const deleteUserGroup = '/user-group/d';
    return this.http.delete<UserGroup[]>(`${this.resourceUrl}${deleteUserGroup}/${id}`);
  }

  deleteSecurityGroup(id: string): Observable<any> {
    const deleteSecurityGroup = '/security-group/d';
    return this.http.delete<UserGroup[]>(`${this.resourceUrl}${deleteSecurityGroup}/${id}`);
  }
}
