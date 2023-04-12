import { Component, TemplateRef, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { IUser, User } from '../../../admin/user-management/user-management.model';
import { Team } from '../../models/team';
import * as moment from 'moment';
import { ContractCategory } from '../../models/contract-category';
import { ContractType } from '../../models/contractType';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { ContractmanangerService } from '../../contractmananger.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../../../core/auth/account.service';
import { DisplayMode, ClauseStatus } from '../../shared/constants/contract-manager.constants';
import { Roles } from '../../shared/constants/user-role.constans';
import { ContractClause } from '../../models/contractClause';
import { ContractAgreement } from '../../models/contractAgreement';
import { ContractTemplateRevision } from '../../models/contractTemplateRevision';
import { ContractClauseRevision } from '../../models/contractClauseRevision';
import { ContractTemplate } from '../../models/contractTemplate';
import { EntityStatus } from '../../shared/constants/entity-status';
import { ApprovalCriteria } from '../../models/approval-criteria';
import { RequestApprovalDialogComponent } from '../../shared/request-approval-dialog/request-approval-dialog.component';
import { ApproveDialogComponent } from '../../shared/approve-dialog/approve-dialog.component';
import { NotificationConfig } from '../../models/notifications-config';
import { ConfirmationDialogComponent } from '../../shared/confirmation-dialog/confirmation-dialog.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-add-contract-clause',
  templateUrl: './add-contract-clause.component.html',
  styleUrls: ['./add-contract-clause.component.scss'],
})
export class AddContractClauseComponent {
  @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;
  @ViewChild('viewClauseDoc') viewClauseDoc!: TemplateRef<any>;

  pageLoading = false;
  contractClause: ContractClause = new ContractClause();
  historyColumns = ['events', 'createdBy', 'createdOn'];
  assignedUsers: any[] = [];
  currentUser: IUser = new User();
  team: Team | undefined;
  teamFields: string[] = [Roles.SecondaryOwner, Roles.Approver];
  clausePdfSrc = '';
  uploadedClauseIdObserver: Observable<any> | undefined = this.contractmanagerService.uploadedClause;
  contractClausesVersions: ContractClause[] | undefined;
  notificationConfig: NotificationConfig = new NotificationConfig();
  notificationConfigForm: FormGroup = this.fb.group({
    notifications: this.fb.array([]),
  });

  teamForm = this.fb.group({
    primaryOwner: [],
    secondaryOwner: [],
    contractAdmin: [],
    approver: [],
    observer: [],
    externalReviewer: [],
  });

  languages = [
    'Afrikaans',
    'Albanian',
    'Arabic',
    'Armenian',
    'Basque',
    'Bengali',
    'Bulgarian',
    'Catalan',
    'Cambodian',
    'Chinese (Mandarin)',
    'Croatian',
    'Czech',
    'Danish',
    'Dutch',
    'English',
    'Estonian',
    'Fiji',
    'Finnish',
    'French',
    'Georgian',
    'German',
    'Greek',
    'Gujarati',
    'Hebrew',
    'Hindi',
    'Hungarian',
    'Icelandic',
    'Indonesian',
    'Irish',
    'Italian',
    'Japanese',
    'Javanese',
    'Korean',
    'Latin',
    'Latvian',
    'Lithuanian',
    'Macedonian',
    'Malay',
    'Malayalam',
    'Maltese',
    'Maori',
    'Marathi',
    'Mongolian',
    'Nepali',
    'Norwegian',
    'Persian',
    'Polish',
    'Portuguese',
    'Punjabi',
    'Quechua',
    'Romanian',
    'Russian',
    'Samoan',
    'Serbian',
    'Slovak',
    'Slovenian',
    'Spanish',
    'Swahili',
    'Swedish',
    'Tamil',
    'Tatar',
    'Telugu',
    'Thai',
    'Tibetan',
    'Tonga',
    'Turkish',
    'Ukrainian',
    'Urdu',
    'Uzbek',
    'Vietnamese',
    'Welsh',
    'Xhosa',
  ];
  // dateFormats = ['MM/DD/YY','DD/MM/YY','YY/MM/DD','Month D, Yr','M/D/YY',
  //   'D/M/YY','YY/M/D','bM/bD/YY','bD/bM/YY','YY/bM/bD','MMDDYY','DDMMYY','YYMMDD','MonDDYY',
  //   // '','','','','','','','','',
  // ];

  dateToDB = moment(new Date()).format('YYYY-MM-DD');
  contractClauseForm = this.fb.group({
    id: [null],
    contractCategory: [new ContractCategory('', '')],
    contractType: [new ContractType()],
    clauseName: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    language: [''],
    clauseCode: [],
    team: this.teamForm,
    isEditable: false,
    isDeviationAnalysis: false,
    isDependentClause: false,
    status: ['DRAFT'],
    clauseUploaded: false,
  });
  displayMode = '';

  get isClauseUploaded(): boolean {
    return <boolean>this.contractClauseForm.get('clauseUploaded')?.value;
  }

  get clauseName(): AbstractControl {
    return <AbstractControl>this.contractClauseForm.get('clauseName');
  }

  get entityRole(): string {
    return this.contractClause.entityRole !== Roles.SecondaryOwner ? this.contractClause.entityRole : Roles.Observer;
  }

  contractTypesByCategoryObserver: Observable<ContractType[]> | undefined;
  contractTypesByCategory: any[] = [];
  contractCategoriesObserver: Observable<ContractCategory[]> | undefined;
  contractCategories: ContractCategory[] = [];
  selectedContractType: any = new Object({ templateNames: [] });

  constructor(
    private fb: FormBuilder,
    private contractmanagerService: ContractmanangerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute,
    private accoutService: AccountService,
    private translate: TranslateService,
    private location: Location
  ) {}

  ngOnInit(): void {
    const paramMap = this.route.snapshot.paramMap;
    const clauseId = paramMap.get('clauseId');

    if (clauseId) {
      this.initViewMode(clauseId);
    } else {
      this.displayMode = DisplayMode.CREATE;
      this.contractClause = new ContractClause();
      this.contractCategoriesObserver = this.contractmanagerService.contractCategories;
      this.contractmanagerService.getContractCategories();
      this.contractCategoriesObserver.subscribe(data => {
        data.forEach(contractCategory => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractCategories[contractCategory.categoryName] = contractCategory;
        });
        // eslint-disable-next-line no-console
        console.log(data);
      });
    }

    this.accoutService.identity().subscribe(account => {
      this.currentUser = Object.assign(this.currentUser, account);
      this.teamForm.patchValue({
        primaryOwner: this.currentUser,
      });
      this.assignedUsers.push({ firstName: this.currentUser.firstName, lastName: this.currentUser.lastName, role: Roles.PrimaryOwner });
    });
  }
  compareObjects(object1: any, object2: any): boolean {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return object1 && object2 && object1.id === object2.id;
  }
  onCategorySelect(selectedCategory: any): void {
    this.getContractTypesByCategory(selectedCategory.id);
  }
  openTeamManagementDialog(mode: string): void {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    this.dialog
      .open(this.teamDialog, {
        width: '70%',
        minHeight: '90%',
      })
      .afterClosed()
      .subscribe(data => {
        this.createAssignedUserList();
      });
  }

  deleteClause(): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: this.translate.instant('contractmanager.message.deleteConfirmation'),
          dialogType: 'delete',
        },
        height: '130px',
      })
      .afterClosed()
      .subscribe((confirmation: boolean) => {
        if (!confirmation) {
          return;
        }

        if (this.contractClause.id && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.contractmanagerService.deleteClause(this.contractClause.id).subscribe(response => {
            this.showConfirmationMessage('Contract Clause Deleted Successfully');
            this.router.navigate(['/contractmanager/agreement/view-contract-clause']);
          });
        }
      });
  }

  deleteMember(user: any): void {
    if (!user.role) {
      return;
    }

    const team = this.teamForm.get(user.role)?.value as IUser[];
    const index = team.findIndex(x => x.id === user.user.id);

    if (index >= 0) {
      team.splice(index, 1);
    }
  }

  getContractTypesByCategory(selectedCategoryId: string | undefined): Observable<ContractType[]> {
    this.contractTypesByCategoryObserver = this.contractmanagerService.contractTypesByCategory;
    this.contractmanagerService.getContractTypesByCategory(selectedCategoryId);
    return this.contractTypesByCategoryObserver;
  }
  // addTemplate(row: any, isToggleOn: boolean): void {}

  onContractTypeSelect(selected_ContractType: any): void {
    this.selectedContractType = selected_ContractType;
  }

  addClause(): void {
    this.contractmanagerService.addContractClause(this.contractClauseForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/clause/view-contract-clauses']).then(() => {
        window.location.reload();
      });
      // eslint-disable-next-line no-console
      // console.log(response);
    });
  }
  saveClause(): void {
    this.contractClauseForm.value.clauseName = this.contractClause.clauseName;
    this.contractmanagerService.updateContractClause(this.contractClauseForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/clause/view-contract-clauses']).then(() => {
        window.location.reload();
      });
      // eslint-disable-next-line no-console
      // console.log(response);
    });
  }
  rollBack(contractClause: ContractClause): void {
    const tempContractClause = new ContractClause();
    tempContractClause.id = contractClause.id;
    tempContractClause.version = contractClause.version;
    this.contractmanagerService.rollBackClause(tempContractClause).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.router.navigate(['/contractmanager/clause/add-contract-clause/' + contractClause.id]).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }
  approveClause(): void {
    this.contractmanagerService.approveClause(this.contractClause).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/clause/view-contract-clauses']).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
    // this.dialogRef.close();
  }

  clauseUploaded($event: string): void {
    this.contractmanagerService.getUploadedClause();
    this.uploadedClauseIdObserver = this.contractmanagerService.uploadedClause;
    this.uploadedClauseIdObserver.subscribe(uploadedClause => {
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.clausePdfSrc = '/content/' + uploadedClause.fileId + '.docx';
      this.contractClauseForm.patchValue({
        clauseUploaded: true,
      });
      this.showConfirmationMessage('Uploaded clause successfully');
    });
  }
  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }
  changeDisplayMode(display_Mode: string): void {
    this.displayMode = display_Mode;
    if (this.displayMode === DisplayMode.UPDATE) {
      this.clauseName.disable();
      this.contractCategoriesObserver = this.contractmanagerService.contractCategories;
      this.contractmanagerService.getContractCategories();
      this.contractCategoriesObserver.subscribe(data => {
        data.forEach(contractCategory => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractCategories[contractCategory.categoryName] = contractCategory;
          if (contractCategory.categoryName === this.contractClause.contractCategory?.categoryName) {
            this.getContractTypesByCategory(contractCategory.id).subscribe(data1 => {
              data1.forEach(contractType => {
                this.contractTypesByCategory.push(contractType);
                this.onContractTypeSelect(this.contractClause.contractCategory);
              });
            });
          }
        });
        // eslint-disable-next-line no-console
        console.log(data);
      });
    }
  }
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  getClauseStatusFlag(): string {
    switch (this.contractClause.status) {
      case ClauseStatus.DRAFT:
        return 'draft';

      case ClauseStatus.APPROVAL_PENDING:
        return 'approval-pending';

      case ClauseStatus.APPROVED:
        return 'approved';

      case ClauseStatus.APPROVAL_REJECTED:
        return 'approval-rejected';

      // case ClauseStatus.EXPIRED:
      //   return 'expired';
    }
  }
  openViewClauseDialog(): void {
    // this.clausePdfSrc = "/content/ContractClause.docx";
    this.dialog
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      .open(this.viewClauseDoc, {
        width: '100%',
        height: '100%',
        // top: '500px'
      })
      .afterClosed()
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      .subscribe(data => {});
  }
  sendForApproval(): void {
    this.contractmanagerService.publishClause(this.contractClause).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/clause/view-contract-clauses']).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }
  getContractClauseRevisions(clauseId: string): void {
    this.contractmanagerService.getClauseVersions(clauseId).subscribe((data: ContractClauseRevision) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (!data || !data.contractClauseRevisions) {
        // this.contractClausesVersions = [];
        return;
      }
      this.contractClausesVersions = data.contractClauseRevisions;
    });
  }

  openRequestApprovalDialog(): void {
    this.dialog
      .open(RequestApprovalDialogComponent, {
        width: '30%',
        disableClose: true,
        data: this.contractClause.team?.approver,
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.sendForApproval();
        }
      });
  }

  rejectClause(): void {
    return;
  }

  openApproveDialog(): void {
    this.dialog
      .open(ApproveDialogComponent, {
        width: '30%',
        disableClose: true,
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.approveClause();
        }
      });
  }

  updateStatus(status: string): void {
    const approvalStatus = this.contractClause.approvalStatus;
    const approvalCriteria = approvalStatus?.approvalCriteria;

    switch (status) {
      case EntityStatus.APPROVAL_PENDING:
        this.contractClause.status = EntityStatus.APPROVAL_PENDING;

        if (
          approvalCriteria === ApprovalCriteria.All &&
          approvalStatus?.userApprovalStatus?.filter(x => x.userId === this.currentUser.id && x.hasApproved)?.length
        ) {
          this.contractClause.status = EntityStatus.WAITING_FOR_OTHERS_APPROVAL;
        }
        break;

      case EntityStatus.REJECTED:
        this.contractClause.status = EntityStatus.REJECTED;
        break;

      case EntityStatus.DRAFT:
        this.contractClause.status = EntityStatus.DRAFT;
        break;

      case EntityStatus.APPROVED:
        this.contractClause.status = EntityStatus.APPROVED;
        break;

      default:
        this.contractClause.status = EntityStatus.ERROR;
    }
  }

  cancel(mode: string): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: this.translate.instant('contractmanager.message.cancelConfirmation'),
          dialogType: 'cancel',
        },
        height: '130px',
      })
      .afterClosed()
      .subscribe((confirmation: boolean) => {
        if (!confirmation) {
          return;
        }

        if (mode === 'create') {
          this.navigateBack();
        } else {
          this.ngOnInit();
        }
      });
  }

  navigateBack(): void {
    this.location.back();
  }

  private createAssignedUserList(): void {
    this.assignedUsers = [];
    if (this.teamForm.value?.primaryOwner) {
      const primaryOwner = this.teamForm.value?.primaryOwner;
      this.assignedUsers.push({ firstName: primaryOwner.firstName, lastName: primaryOwner.lastName, role: Roles.PrimaryOwner });
    }
    if (this.teamForm.value?.approver) {
      const approver = this.teamForm.value?.approver;
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      approver.forEach(user => {
        this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: Roles.Approver, id: user.id });
      });
    }
    if (this.teamForm.value?.secondaryOwner) {
      const secondaryOwner = this.teamForm.value?.secondaryOwner;
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      secondaryOwner.forEach(user => {
        this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: Roles.SecondaryOwner, id: user.id });
      });
    }
  }

  private initViewMode(clauseId: string): void {
    this.pageLoading = true;
    this.displayMode = DisplayMode.VIEW;
    this.contractmanagerService.getContractClause(clauseId).subscribe((cc: ContractClause) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (cc) {
        this.contractClause = cc;
        this.contractClauseForm.patchValue({
          id: cc.id,
          contractCategory: new Object({ id: cc.contractCategory?.id, categoryName: cc.contractCategory?.categoryName }),
          contractType: new Object({ id: cc.contractType?.id, name: cc.contractType?.name }),
          clauseName: cc.clauseName,
          language: cc.language,
          status: cc.status,
          description: cc.description,
          isEditable: cc.isEditable,
          isDeviationAnalysis: cc.isDeviationAnalysis,
          isDependentClause: cc.isDependentClause,
          version: cc.version,
          updatedOn: cc.updatedOn,
          team: cc.team,
        });
        // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
        this.clausePdfSrc = '/content/' + cc.tempFileName + '.docx';
        this.createAssignedUserList();
      }
      this.updateStatus(cc.status ?? EntityStatus.ERROR);
      this.pageLoading = false;
    });
    this.getContractClauseRevisions(clauseId);
  }
}
