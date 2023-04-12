import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserGroup } from '../../../models/user-group';
import { UserManagementService } from '../../user-management.service';

@Component({
  selector: 'jhi-view-user-groups',
  templateUrl: './view-user-groups.component.html',
  styleUrls: ['./view-user-groups.component.scss'],
})
export class ViewUserGroupsComponent implements AfterViewInit {
  userGroups: UserGroup[] = [];
  userGroupObserver: Observable<UserGroup[]> | undefined;

  userGroupsDataSource = new MatTableDataSource<any>([]);
  userGroupColumns: string[] = ['name', 'createdBy', 'createdOn', 'action'];

  @ViewChild(MatSort) sort!: MatSort;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private userManagementService: UserManagementService, private router: Router) {}

  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.userGroupObserver = this.userManagementService.getUserGroups();
    this.userGroupObserver.subscribe((userGroups: UserGroup[]) => {
      if (userGroups.length > 0) {
        this.userGroups = userGroups;
        this.userGroupsDataSource = new MatTableDataSource<any>(this.userGroups);
        this.userGroupsDataSource.sort = this.sort;
        this.userGroupsDataSource.paginator = this.paginator;
      }
    });
  }

  navigateTo(ct: UserGroup): void {
    this.router.navigate(['contractmanager/user-management/user-groups/view-user-group', ct.id]);
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdDate', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
