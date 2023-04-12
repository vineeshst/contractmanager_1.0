import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ContractmanangerService } from '../../contractmananger.service';
import { ContractClause } from '../../models/contractClause';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-view-contract-clauses',
  templateUrl: './view-contract-clauses.component.html',
  styleUrls: ['./view-contract-clauses.component.scss'],
})
export class ViewContractClausesComponent implements AfterViewInit {
  pageLoading = false;
  contractClauses: ContractClause[] = [];
  contractClausesObserver: Observable<ContractClause[]> | undefined;

  contractClausesDataSource = new MatTableDataSource<any>([]);
  contractClausesColumns: string[] = ['clauseName', 'contractTypeName', 'createdBy', 'createdOn', 'status', 'action'];

  @ViewChild(MatSort) sort!: MatSort;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  constructor(private contractManagerService: ContractmanangerService, private router: Router) {}

  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.pageLoading = true;
    this.contractClausesObserver = this.contractManagerService.contractClauses;
    this.contractManagerService.getContractClauses();
    this.contractClausesObserver.subscribe((contractClauses: ContractClause[]) => {
      if (contractClauses.length > 0) {
        const tempContractClauses: any[] | undefined = [];
        contractClauses.forEach(contractClause => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractClauses[contractClause.name] = contractClause;
          tempContractClauses.push({
            id: contractClause.id,
            clauseName: contractClause.clauseName,
            contractType: contractClause.contractType,
            language: contractClause.language,
            createdBy: contractClause.createdBy,
            createdDate: contractClause.createdDate,
            status: contractClause.status,
          });
        });
        this.contractClausesDataSource = new MatTableDataSource<any>(tempContractClauses);
        this.contractClausesDataSource.sort = this.sort;
        this.contractClausesDataSource.paginator = this.paginator;
      }
      this.pageLoading = false;
    });
  }

  navigateTo(cc: ContractClause): void {
    this.router.navigate(['contractmanager/clause/add-contract-clause', cc.id]);
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdDate', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
