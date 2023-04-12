import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { IUser } from '../../../../core/user/user.model';
import { UserManagementService } from '../../../../admin/user-management/service/user-management.service';

@Component({
  selector: 'jhi-view-user',
  templateUrl: './view-user.component.html',
  styleUrls: ['./view-user.component.scss'],
})
export class ViewUserComponent implements AfterViewInit {
  users: IUser[] = [];
  usersObserver!: Observable<HttpResponse<IUser[]>>;

  usersDataSource = new MatTableDataSource<any>([]);
  usersColumns: string[] = ['name', 'createdBy', 'createdOn', 'action'];

  @ViewChild(MatSort) sort!: MatSort;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private userManagementService: UserManagementService, private router: Router) {}

  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.userManagementService.query().subscribe(response => {
      if (response.status === HttpStatusCode.Ok) {
        this.usersDataSource = new MatTableDataSource<any>(response.body ?? []);
        this.usersDataSource.sort = this.sort;
        this.usersDataSource.paginator = this.paginator;
      }
    });
  }

  navigateTo(ct: IUser): void {
    this.router.navigate(['contractmanager/user-management/user/view-user', ct.email]);
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdDate', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
