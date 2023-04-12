import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { ContractmanangerService } from '../../contractmananger.service';
import { ContractType } from '../../models/contractType';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ActivatedRoute, Router } from '@angular/router';
import { EntityStatus } from '../../shared/constants/entity-status';

@Component({
  selector: 'jhi-view-contract-type',
  templateUrl: './view-contract-types.component.html',
  styleUrls: ['./view-contract-types.component.scss'],
})
export class ViewContractTypesComponent implements OnInit, AfterViewInit {
  pageLoading = false;
  contractTypes: ContractType[] = [];
  contractTypesObserver: Observable<ContractType[]> | undefined;

  contractTypesDataSource = new MatTableDataSource<any>([]);
  contractTypeColumns: string[] = ['name', 'contractType', 'CreatedBy', 'CreatedOn', 'status', 'action'];

  @ViewChild(MatSort) sort!: MatSort;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private dialog: MatDialog,
    private contractManagerService: ContractmanangerService,
    private router: Router,
    private route: ActivatedRoute
  ) {}
  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.pageLoading = true;
    this.contractTypesObserver = this.contractManagerService.contractTypes;
    this.contractManagerService.getContractTypes();
    this.contractTypesObserver.subscribe((contractTypes: ContractType[]) => {
      if (contractTypes.length > 0) {
        const tempContractTypes: any[] | undefined = [];
        contractTypes.forEach(contractType => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractTypes[contractType.name] = contractType;
          tempContractTypes.push({
            name: contractType.name,
            contractCategory: contractType.contractCategory!.categoryName,
            updatedOn: contractType.updatedOn,
            action: '',
            id: contractType.id,
            contractType: 'Agreement',
            createdOn: contractType.createdDate,
            createdBy: contractType.createdBy,
            status: contractType.status,
          });
        });
        this.contractTypesDataSource = new MatTableDataSource<any>(tempContractTypes);
        this.contractTypesDataSource.sort = this.sort;
        this.contractTypesDataSource.paginator = this.paginator;
      }
      this.pageLoading = false;
    });
  }

  navigateTo(ct: ContractType): void {
    this.router.navigate(['contractmanager/configuration/view-contract-type/agreement/', ct.id]);
  }

  getDisplayStatus(status: string): string {
    switch (status) {
      case EntityStatus.DRAFT:
        return 'DRAFT';

      // case AgreementStatus.CREATED:
      //   return 'created';

      case EntityStatus.APPROVAL_PENDING:
        return 'APPROVAL PENDING';

      case EntityStatus.APPROVED:
        return 'APPROVED';

      case EntityStatus.APPROVAL_REJECTED:
        return 'APPROVAL REJECTED';

      case EntityStatus.SENT_FOR_SIGNATURE:
        return 'SENT FOR SIGNATURE';

      case EntityStatus.SIGNER_REJECTED:
        return 'SIGNER REJECTED';

      case EntityStatus.COMPLETED:
        return 'COMPLETED';

      case EntityStatus.CANCELED:
        return 'CANCELED';

      case EntityStatus.EXPIRED:
        return 'EXPIRED';

      case EntityStatus.REJECTED:
        return 'APPROVAL REJECTED';

      default:
        return 'N/A';
    }
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdOn', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
