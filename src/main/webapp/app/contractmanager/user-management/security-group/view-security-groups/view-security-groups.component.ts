import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { ContractmanangerService } from '../../../contractmananger.service';
import { ContractType } from '../../../models/contractType';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { SecurityGroup } from '../../../models/security-group';
import { UserManagementService } from '../../user-management.service';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-view-security-groups',
  templateUrl: './view-security-groups.component.html',
  styleUrls: ['./view-security-groups.component.scss'],
})
export class ViewSecurityGroupsComponent implements AfterViewInit {
  securityGroups: SecurityGroup[] = [];
  securityGroupObserver: Observable<SecurityGroup[]> | undefined;

  securityGroupsDataSource = new MatTableDataSource<any>([]);
  securityGroupColumns: string[] = ['name', 'createdBy', 'createdOn', 'action'];

  @ViewChild(MatSort) sort!: MatSort;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private dialog: MatDialog, private userManagementService: UserManagementService, private router: Router) {}

  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.securityGroupObserver = this.userManagementService.getSecurityGroups();
    this.securityGroupObserver.subscribe((securityGroups: SecurityGroup[]) => {
      if (securityGroups.length > 0) {
        const tempContractTypes: any[] | undefined = [];
        securityGroups.forEach(securityGroup => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.securityGroups[securityGroup.name] = securityGroup;
          tempContractTypes.push({
            id: securityGroup.id,
            name: securityGroup.name,
            createdOn: securityGroup.createdDate,
            createdBy: securityGroup.createdByUser,
          });
        });
        this.securityGroupsDataSource = new MatTableDataSource<any>(tempContractTypes);
        this.securityGroupsDataSource.sort = this.sort;
        this.securityGroupsDataSource.paginator = this.paginator;
      }
    });
  }

  navigateTo(ct: ContractType): void {
    this.router.navigate(['contractmanager/user-management/security-group/view-security-group', ct.id]);
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdOn', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
