import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { Task, TaskType } from '../../models/task';
import { DashboardService } from '../dashboard.service';
import { ContractAgreement } from '../../models/contractAgreement';
import { ContractmanangerService } from '../../contractmananger.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { EntityStatus } from '../../shared/constants/entity-status';

@Component({
  selector: 'jhi-my-tasks',
  templateUrl: './my-tasks.component.html',
  styleUrls: ['./my-tasks.component.scss'],
})
export class MyTasksComponent implements OnInit {
  myTasksDataSource = new MatTableDataSource<any>([]);
  myTasksColumns: string[] = ['taskName', 'recordName', 'primaryOwner', 'receivedOn', 'dueBy'];

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  constructor(public dashboardService: DashboardService, private router: Router, private contractManagerService: ContractmanangerService) {}

  ngOnInit(): void {
    this.dashboardService.getMyTasks().subscribe(tasks => {
      this.myTasksDataSource = new MatTableDataSource<Task>(tasks);
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
