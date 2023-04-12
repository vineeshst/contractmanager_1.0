import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Observable } from 'rxjs';
import { ContractTemplate } from '../../models/contractTemplate';
import { ContractAgreement } from '../../models/contractAgreement';
import { ContractmanangerService } from '../../contractmananger.service';
import { Router } from '@angular/router';
import { AgreementStatus } from '../../shared/constants/contract-manager.constants';
@Component({
  selector: 'jhi-view-contract-agreements',
  templateUrl: './view-contract-agreements.component.html',
  styleUrls: ['./view-contract-agreements.component.scss'],
})
export class ViewContractAgreementsComponent implements OnInit, AfterViewInit {
  pageLoading = false;
  agreements: ContractAgreement[] = [];
  agreementsObserver: Observable<ContractAgreement[]> | undefined;
  agreementsDataSource = new MatTableDataSource<any>([]);
  agreementsColumns: string[] = ['agreementName', 'contractTypeName', 'createdBy', 'createdOn', 'agreementStatus', 'action'];
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  @ViewChild(MatSort) sort: MatSort;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  @ViewChild(MatPaginator) paginator: MatPaginator;
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  constructor(private contractManagerService: ContractmanangerService, private router: Router) {
    //do nothing
  }
  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.pageLoading = true;
    this.agreementsObserver = this.contractManagerService.contractAgreements;
    this.contractManagerService.getContractAgreements();
    this.agreementsObserver.subscribe((contractAgreements: ContractAgreement[]) => {
      if (contractAgreements.length > 0) {
        const tempAgreements: any[] | undefined = [];
        contractAgreements.forEach(contractAgreement => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.agreements[contractAgreement.agreementName] = contractAgreement;
          tempAgreements.push({
            id: contractAgreement.id,
            agreementName: contractAgreement.agreementName,
            // contractCategory: contractAgreement.contractCategory,
            contractType: contractAgreement.contractType,
            // updatedOn: contractAgreement.updatedOn,
            createdBy: contractAgreement.createdBy,
            createdDate: contractAgreement.createdDate,
            agreementStatus: contractAgreement.agreementStatus,
          });
        });
        this.agreementsDataSource = new MatTableDataSource<any>(tempAgreements);
        this.agreementsDataSource.sort = this.sort;
        this.agreementsDataSource.paginator = this.paginator;
      }
      this.pageLoading = false;
    });
  }

  navigateTo(ca: ContractAgreement): void {
    this.router.navigate(['contractmanager/agreement/add-contract-agreement', ca.id]);
  }

  getDisplayStatus(status: string): string {
    switch (status) {
      case AgreementStatus.DRAFT:
        return 'DRAFT';

      // case AgreementStatus.CREATED:
      //   return 'created';

      case AgreementStatus.APPROVAL_PENDING:
        return 'APPROVAL PENDING';

      case AgreementStatus.APPROVED:
        return 'APPROVED';

      case AgreementStatus.APPROVAL_REJECTED:
        return 'APPROVAL REJECTED';

      case AgreementStatus.SENT_FOR_SIGNATURE:
        return 'SENT FOR SIGNATURE';

      case AgreementStatus.SIGNER_REJECTED:
        return 'SIGNER REJECTED';

      case AgreementStatus.COMPLETED:
        return 'COMPLETED';

      case AgreementStatus.CANCELED:
        return 'CANCELED';

      case AgreementStatus.EXPIRED:
        return 'EXPIRED';

      case AgreementStatus.REJECTED:
        return 'APPROVAL REJECTED';

      default:
        return 'N/A';
    }
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdDate', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
