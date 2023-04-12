import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Location } from '@angular/common';

import { ContractmanangerService } from '../../contractmananger.service';
import { ContractCategory } from '../../models/contract-category';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { ContractType } from '../../models/contractType';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Team } from '../../models/team';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { ContractTemplate } from '../../models/contractTemplate';
import { IUser, User } from '../../../admin/user-management/user-management.model';
import { AgreementStatus, DisplayMode, TemplateStatus } from '../../shared/constants/contract-manager.constants';
import { Roles } from '../../shared/constants/user-role.constans';
import { AccountService } from '../../../core/auth/account.service';
import { SERVER_API_URL } from '../../../app.constants';
import { ContractClause } from '../../models/contractClause';
import { ContractTemplateRevision } from '../../models/contractTemplateRevision';
import { ContractAgreement } from '../../models/contractAgreement';
import { RequestApprovalDialogComponent } from '../../shared/request-approval-dialog/request-approval-dialog.component';
import { ApproveDialogComponent } from '../../shared/approve-dialog/approve-dialog.component';
import { ApprovalCriteria } from '../../models/approval-criteria';
import { EntityStatus } from '../../shared/constants/entity-status';
import { NotificationConfig } from '../../models/notifications-config';
import { ConfirmationDialogComponent } from '../../shared/confirmation-dialog/confirmation-dialog.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-add-contract-template',
  templateUrl: './add-contract-template.component.html',
  styleUrls: ['./add-contract-template.component.scss'],
})
export class AddContractTemplateComponent implements OnInit {
  @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;
  @ViewChild('viewTemplateDoc') viewTemplateDoc!: TemplateRef<any>;

  pageLoading = false;
  contractTemplate: ContractTemplate = new ContractTemplate();
  historyColumns = ['events', 'createdBy', 'createdOn'];
  assignedUsers: any[] = [];
  currentUser: IUser = new User();
  team: Team | undefined;
  // teamFields: string[] = ['primaryOwner', 'secondaryOwner', 'approver'];
  teamFields: string[] = [Roles.SecondaryOwner, Roles.Approver];
  templatePdfSrc = '';
  uploadedTemplateIdObserver: Observable<any> | undefined = this.contractmanagerService.uploadedTemplate;
  contractTemplatesVersions: ContractTemplate[] | undefined;
  notificationConfig: NotificationConfig = new NotificationConfig();
  notificationConfigForm: FormGroup = this.fb.group({
    notifications: this.fb.array([]),
  });
  templateId: string | null = null;

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
  dateFormats = [
    'MM/DD/YY',
    'DD/MM/YY',
    'YY/MM/DD',
    'Month D, Yr',
    'M/D/YY',
    'D/M/YY',
    'YY/M/D',
    'bM/bD/YY',
    'bD/bM/YY',
    'YY/bM/bD',
    'MMDDYY',
    'DDMMYY',
    'YYMMDD',
    'MonDDYY',
    // '','','','','','','','','',
  ];

  dateToDB = moment(new Date()).format('YYYY-MM-DD');
  contractTemplateForm = this.fb.group({
    id: [null],
    contractCategory: [new ContractCategory('', '')],
    contractType: [new ContractType()],
    templateName: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    language: [''],
    secondaryLanguage: [''],
    dateFormat: [''],
    effectiveFrom: ['', Validators.required],
    effectiveTo: ['', Validators.required],
    templateCode: [],
    team: this.teamForm,
    primary: false,
    status: ['DRAFT'],
    templateUploaded: false,
    tempFileName: '',
  });
  //  isEditMode = false;
  // isLinear = true;
  displayMode = '';

  get isTemplateUploaded(): boolean {
    return <boolean>this.contractTemplateForm.get('templateUploaded')?.value;
  }

  get templateName(): AbstractControl {
    return <AbstractControl>this.contractTemplateForm.get('templateName');
  }
  get effectiveFrom(): string {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return moment(this.contractTemplateForm.get('effectiveFrom').value).format('ddd, MMM Do YYYY');
  }
  get effectiveTo(): string {
    // return this.contractTemplateForm.get('effectiveTo');
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return moment(this.contractTemplateForm.get('effectiveTo').value).format('ddd, MMM Do YYYY');
  }

  get entityRole(): string {
    return this.contractTemplate.entityRole !== Roles.SecondaryOwner ? this.contractTemplate.entityRole : Roles.Observer;
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
    private location: Location,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const paramMap = this.route.snapshot.paramMap;
    this.templateId = paramMap.get('templateId');

    if (this.templateId) {
      this.initViewMode(this.templateId);
    } else {
      this.displayMode = DisplayMode.CREATE;
      this.contractTemplate = new ContractTemplate();
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

    // this.contractCategoriesObserver = this.contractmanagerService.contractCategories;
    // this.contractmanagerService.getContractCategories();
    // this.contractCategoriesObserver.subscribe(data => {
    //   data.forEach(contractCategory => {
    //     // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //     // @ts-ignore
    //     this.contractCategories[contractCategory.categoryName] = contractCategory;
    //   });
    //   // eslint-disable-next-line no-console
    //   console.log(data);
    // });
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
    // for (let i = 0; i < this.contractTypesByCategory.length; i++) {
    //   if (this.contractTypesByCategory[i].name === selectedContractTypeName) {
    //     this.selectedContractType = this.contractTypesByCategory[i];
    //     break;
    //   }
    // }
  }

  addTemplate(): void {
    this.contractmanagerService.addContractTemplate(this.contractTemplateForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/template/view-contract-templates']).then(() => {
        window.location.reload();
      });
      // eslint-disable-next-line no-console
      // console.log(response);
    });
  }
  saveTemplate(): void {
    this.contractTemplateForm.value.templateName = this.contractTemplate.templateName;
    this.contractmanagerService.updateContractTemplate(this.contractTemplateForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      if (this.templateId) {
        this.initViewMode(this.templateId);
      }
    });
  }
  rollBack(contractTemplate: ContractTemplate): void {
    const tempContractTemplate = new ContractTemplate();
    tempContractTemplate.id = contractTemplate.id;
    tempContractTemplate.version = contractTemplate.version;
    this.contractmanagerService.rollBackTemplate(tempContractTemplate).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.router.navigate(['/contractmanager/template/add-contract-template/' + contractTemplate.id]).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }
  approveTemplate(): void {
    this.contractmanagerService.approveTemplate(this.contractTemplate).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/template/view-contract-templates']).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
    // this.dialogRef.close();
  }

  templateUploaded($event: string): void {
    // this.isTemplatedUploaded = true;
    this.contractmanagerService.getUploadedTemplate();
    this.uploadedTemplateIdObserver = this.contractmanagerService.uploadedTemplate;
    this.uploadedTemplateIdObserver.subscribe(uploadedTemplate => {
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.templatePdfSrc = '/content/' + uploadedTemplate.fileId + '.docx';
      this.contractTemplateForm.patchValue({
        templateUploaded: true,
      });
      this.showConfirmationMessage('Uploaded template successfully');
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
      this.templateName.disable();
      this.contractCategoriesObserver = this.contractmanagerService.contractCategories;
      this.contractmanagerService.getContractCategories();
      this.contractCategoriesObserver.subscribe(data => {
        data.forEach(contractCategory => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractCategories[contractCategory.categoryName] = contractCategory;
          if (contractCategory.categoryName === this.contractTemplate.contractCategory?.categoryName) {
            this.getContractTypesByCategory(contractCategory.id).subscribe(data1 => {
              data1.forEach(contractType => {
                this.contractTypesByCategory.push(contractType);
                this.onContractTypeSelect(this.contractTemplate.contractCategory);
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
  getTemplateStatusFlag(): string {
    switch (this.contractTemplate.status) {
      case TemplateStatus.DRAFT:
        return 'draft';

      case TemplateStatus.APPROVAL_PENDING:
        return 'approval-pending';

      case TemplateStatus.APPROVED:
        return 'approved';

      case TemplateStatus.APPROVAL_REJECTED:
        return 'approval-rejected';

      // case TemplateStatus.EXPIRED:
      //   return 'expired';
    }
  }
  openViewTemplateDialog(): void {
    // this.templatePdfSrc = "/content/ContractTemplate.docx";
    // this.templatePdfSrc = SERVER_API_URL + 'api/contracts/templates/upload';
    // this.templatePdfSrc =  '/api/contracts/templates/upload';
    // this.templatePdfSrc = 'C:\\Users\\Krian GMBH\\AppData\\Local\\Temp\\3285097173598087381_Template_Uploaded.docx'
    this.dialog
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      .open(this.viewTemplateDoc, {
        width: '100%',
        height: '100%',
        // top: '500px'
      })
      .afterClosed()
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      .subscribe(data => {});
  }
  sendForApproval(): void {
    this.contractmanagerService.publishTemplate(this.contractTemplate).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/template/view-contract-templates']).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }

  deleteContractTemplate(): void {
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

        if (this.templateId && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.contractmanagerService.deleteTemplate(this.templateId).subscribe(response => {
            this.showConfirmationMessage('Contract Template Deleted Successfully');
            this.router.navigate(['/contractmanager/agreement/view-contract-templates']);
          });
        }
      });
  }

  getContractTemplateRevisions(templateId: string): void {
    this.contractmanagerService.getTemplateVersions(templateId).subscribe((data: ContractTemplateRevision) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (!data || !data.contractTemplateRevisions) {
        // this.contractTemplatesVersions = [];
        return;
      }
      this.contractTemplatesVersions = data.contractTemplateRevisions;
    });
  }

  openRequestApprovalDialog(): void {
    this.dialog
      .open(RequestApprovalDialogComponent, {
        width: '30%',
        disableClose: true,
        data: this.contractTemplate.team?.approver,
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.sendForApproval();
        }
      });
  }

  rejectContractTemplate(): void {
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
          this.approveTemplate();
        }
      });
  }

  updateStatus(status: string): void {
    const approvalStatus = this.contractTemplate.approvalStatus;
    const approvalCriteria = approvalStatus?.approvalCriteria;

    switch (status) {
      case EntityStatus.APPROVAL_PENDING:
        this.contractTemplate.status = AgreementStatus.APPROVAL_PENDING;

        if (
          approvalCriteria === ApprovalCriteria.All &&
          approvalStatus?.userApprovalStatus?.filter(x => x.userId === this.currentUser.id && x.hasApproved)?.length
        ) {
          this.contractTemplate.status = EntityStatus.WAITING_FOR_OTHERS_APPROVAL;
        }
        break;

      case EntityStatus.REJECTED:
        this.contractTemplate.status = EntityStatus.REJECTED;
        break;

      case EntityStatus.DRAFT:
        this.contractTemplate.status = EntityStatus.DRAFT;
        break;

      case EntityStatus.APPROVED:
        this.contractTemplate.status = EntityStatus.APPROVED;
        break;

      default:
        this.contractTemplate.status = AgreementStatus.ERROR;
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

  private initViewMode(templateId: string): void {
    this.pageLoading = true;
    this.displayMode = DisplayMode.VIEW;

    this.contractmanagerService.getContractTemplate(templateId).subscribe((ct: ContractTemplate) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (ct) {
        this.contractTemplate = ct;
        this.contractTemplateForm.patchValue({
          id: ct.id,
          contractCategory: new Object({ id: ct.contractCategory?.id, categoryName: ct.contractCategory?.categoryName }),
          contractType: new Object({ id: ct.contractType?.id, name: ct.contractType?.name }),
          templateName: ct.templateName,
          language: ct.language,
          secondaryLanguage: ct.secondaryLanguage,
          dateFormat: ct.dateFormat,
          effectiveFrom: ct.effectiveFrom,
          effectiveTo: ct.effectiveTo,
          status: ct.status,
          isPrimary: ct.isPrimary,
          description: ct.description,
          clauseNames: ct.clauseNames,
          version: ct.version,
          updatedOn: ct.updatedOn,
          team: ct.team,
          tempFileName: ct.tempFileName,
        });
        // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
        this.templatePdfSrc = '/content/' + ct.tempFileName + '.docx';
        this.createAssignedUserList();
      }
      this.updateStatus(ct.status ?? EntityStatus.ERROR);
      this.pageLoading = false;
    });
    this.getContractTemplateRevisions(templateId);
  }
}
