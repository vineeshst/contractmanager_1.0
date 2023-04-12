import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountService } from '../../../core/auth/account.service';
import { LoginService } from '../../../login/login.service';
import { Router } from '@angular/router';
import { ContractmanangerService } from '../../../contractmanager/contractmananger.service';
import { ContractAgreement } from '../../models/contractAgreement';
import { observable, Observable } from 'rxjs';
import { FormControl } from '@angular/forms';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'jhi-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  profileName: string;
  filterList?: Observable<ContractAgreement[]>;
  agreements?: ContractAgreement[];
  searchKey = '';

  @Output()
  toggle = new EventEmitter<string>();

  @Input() hasNotification!: boolean;

  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
    private router: Router,
    private contractManagerService: ContractmanangerService
  ) {
    this.profileName = '';
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (account != null) {
        // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
        this.profileName = account.firstName + ' ' + account.lastName;
      }
    });

    this.filterList = this.contractManagerService.contractAgreements;
    this.contractManagerService.getContractAgreements();
    this.filterList.subscribe(agreements => {
      this.agreements = agreements;
    });
  }

  logout(): void {
    // this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  searchClose(): void {
    this.searchKey = '';
  }

  toggleNavigation(): void {
    this.toggle.emit();
  }
}
