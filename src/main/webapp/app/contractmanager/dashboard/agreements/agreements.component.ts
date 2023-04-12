import { Component, OnInit } from '@angular/core';
import { Sort } from '@angular/material/sort';
import { ActivatedRoute } from '@angular/router';
import { ContractmanangerService } from '../../contractmananger.service';
import { ContractAgreement } from '../../models/contractAgreement';
import { EntityStatus } from '../../shared/constants/entity-status';

@Component({
  selector: 'jhi-agreements',
  templateUrl: './agreements.component.html',
  styleUrls: ['./agreements.component.scss'],
})
export class AgreementsComponent implements OnInit {
  columns: string[] = ['agreementName', 'agreementCode', 'contractTypeName', 'createdBy', 'createdOn', 'agreementStatus', 'action'];
  agreements: ContractAgreement[] = [];
  pageLoading = true;

  currentFilter!: string;

  constructor(private route: ActivatedRoute, private contractManagerService: ContractmanangerService) {
    this.currentFilter = this.route.snapshot.queryParamMap.get('q') ?? 'a';
  }
  ngOnInit(): void {
    switch (this.currentFilter) {
      case 'a':
        this.contractManagerService.getContractAgreementsByStatus().subscribe((agreements: ContractAgreement[]) => {
          this.agreements = agreements;
          this.pageLoading = false;
        });
        break;

      case 'pa':
        this.contractManagerService
          .getContractAgreementsByStatus(EntityStatus.APPROVAL_PENDING)
          .subscribe((agreements: ContractAgreement[]) => {
            this.agreements = agreements;
            this.pageLoading = false;
          });
        break;

      case 'ea':
        this.contractManagerService.getContractAgreementsByStatus().subscribe((agreements: ContractAgreement[]) => {
          this.agreements = agreements;
          this.pageLoading = false;
        });
        break;

      case 'pe':
        this.contractManagerService
          .getContractAgreementsByStatus(EntityStatus.SENT_FOR_SIGNATURE)
          .subscribe((agreements: ContractAgreement[]) => {
            this.agreements = agreements;
            this.pageLoading = false;
          });
        break;
    }
  }
}
