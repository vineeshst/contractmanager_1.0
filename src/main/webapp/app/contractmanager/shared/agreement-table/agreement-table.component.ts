import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { BehaviorSubject, Subject } from 'rxjs';
import { ContractAgreement } from '../../models/contractAgreement';
import { AgreementStatus } from '../constants/contract-manager.constants';

@Component({
  selector: 'jhi-agreement-table',
  templateUrl: './agreement-table.component.html',
  styleUrls: ['./agreement-table.component.scss'],
})
export class AgreementTableComponent implements AfterViewInit {
  @Input() columns: string[] = [];
  @Input() agreements: ContractAgreement[] = [];
  @Input() showPaginator = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  @ViewChild(MatSort) sort!: MatSort;

  agreementsDataSource = new MatTableDataSource<ContractAgreement>([]);

  constructor(private router: Router) {}

  ngAfterViewInit(): void {
    this.agreementsDataSource = new MatTableDataSource(this.agreements);
    this.agreementsDataSource.paginator = this.paginator;
    this.agreementsDataSource.sort = this.sort;
    this.sortData();
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
