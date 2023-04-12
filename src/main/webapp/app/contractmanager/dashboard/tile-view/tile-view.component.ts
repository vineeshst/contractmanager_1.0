import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { ContractmanangerService } from '../../contractmananger.service';
import { ContractAgreement } from '../../models/contractAgreement';
import { Task, TaskType } from '../../models/task';
import { EntityStatus } from '../../shared/constants/entity-status';
import { DashboardService } from '../dashboard.service';
@Component({
  selector: 'jhi-tile-view',
  templateUrl: './tile-view.component.html',
  styleUrls: ['./tile-view.component.scss'],
})
export class TileViewComponent {
  dashboard: any = null;
  myTasksDataSource = new MatTableDataSource<any>([]);
  myTasksColumns: string[] = ['taskName', 'recordName', 'receivedOn'];
  switch_expression = 'ct';
  allAgreements: ContractAgreement[] = [];
  waitingForApproval: ContractAgreement[] = [];
  pendingExecution: ContractAgreement[] = [];
  agreementsColumns: string[] = ['agreementName', 'contractTypeName', 'createdBy', 'createdOn', 'agreementStatus', 'action'];
  choiceIsGrid = true;

  constructor(
    private dashboardService: DashboardService,
    private router: Router,
    private contractManagerService: ContractmanangerService
  ) {}

  ngOnInit(): void {
    this.dashboardService.getDashbaordCount().subscribe(dashboard => {
      this.dashboard = dashboard;
    });
    this.dashboardService.getMyTasks().subscribe(tasks => {
      this.myTasksDataSource = new MatTableDataSource<Task>(tasks);
    });
    this.contractManagerService.getContractAgreementsByStatus().subscribe((agreements: ContractAgreement[]) => {
      this.allAgreements = agreements;
      this.waitingForApproval = agreements.filter(x => x.agreementStatus === EntityStatus.APPROVAL_PENDING);
      this.pendingExecution = agreements.filter(x => x.agreementStatus === EntityStatus.SENT_FOR_SIGNATURE);
    });
  }

  route(entityType: string, entityId: string): void {
    switch (entityType) {
      case TaskType.CONTRACT_TYPE_APPROVAL:
        this.router.navigate(['/contractmanager/configuration/view-contract-type/agreement/', entityId]);
        break;

      case TaskType.CONTRACT_AGREEMENT_APPROVAL:
        this.router.navigate(['/contractmanager/agreement/add-contract-agreement/', entityId]);
        break;

      case TaskType.TEMPLATE_APPROVAL:
        this.router.navigate(['/contractmanager/template/add-contract-template/', entityId]);
        break;

      case TaskType.CLAUSE_APPROVAL:
        this.router.navigate(['/contractmanager/clause/add-contract-clause/', entityId]);
        break;

      case TaskType.CONTRACT_AGREEMENT_SIGNATURE:
        this.router.navigate(['/contractmanager/agreement/add-contract-agreement/', entityId]);
        break;

      default:
        this.router.navigate(['/404']);
    }
  }
}
