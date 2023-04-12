import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';
import { ContractTemplate } from '../../models/contractTemplate';
import { ContractType } from '../../models/contractType';
import { MatDialog } from '@angular/material/dialog';
import { ContractmanangerService } from '../../contractmananger.service';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-view-contract-templates',
  templateUrl: './view-contract-templates.component.html',
  styleUrls: ['./view-contract-templates.component.scss'],
})
export class ViewContractTemplatesComponent implements OnInit, AfterViewInit {
  pageLoading = false;
  contractTemplates: ContractTemplate[] = [];
  contractTemplatesObserver: Observable<ContractTemplate[]> | undefined;

  contractTemplatesDataSource = new MatTableDataSource<any>([]);
  // contractTemplatesColumns: string[] = ['templateName', 'contractTypeName', 'language', 'createdBy' ,'createdOn', 'status', 'action'];
  contractTemplatesColumns: string[] = ['templateName', 'contractTypeName', 'createdBy', 'createdOn', 'status', 'action'];

  @ViewChild(MatSort) sort!: MatSort;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  constructor(private contractManagerService: ContractmanangerService, private router: Router) {}
  ngAfterViewInit(): void {
    this.sortData();
  }

  ngOnInit(): void {
    this.pageLoading = true;
    this.contractTemplatesObserver = this.contractManagerService.contractTemplates;
    this.contractManagerService.getContractTemplates();
    this.contractTemplatesObserver.subscribe((contractTemplates: ContractTemplate[]) => {
      if (contractTemplates.length > 0) {
        const tempContractTemplates: any[] | undefined = [];
        contractTemplates.forEach(contractTemplate => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractTemplates[contractTemplate.name] = contractTemplate;
          tempContractTemplates.push({
            id: contractTemplate.id,
            templateName: contractTemplate.templateName,
            contractType: contractTemplate.contractType,
            language: contractTemplate.language,
            createdBy: contractTemplate.createdBy,
            createdDate: contractTemplate.createdDate,
            status: contractTemplate.status,
          });
        });
        this.contractTemplatesDataSource = new MatTableDataSource<any>(tempContractTemplates);
        this.contractTemplatesDataSource.sort = this.sort;
        this.contractTemplatesDataSource.paginator = this.paginator;
      }
      this.pageLoading = false;
    });
  }

  navigateTo(ct: ContractTemplate): void {
    this.router.navigate(['contractmanager/template/add-contract-template', ct.id]);
  }

  private sortData(): void {
    const sortState: Sort = { active: 'createdDate', direction: 'desc' };
    this.sort.active = sortState.active;
    this.sort.direction = sortState.direction;
    this.sort.sortChange.emit(sortState);
  }
}
