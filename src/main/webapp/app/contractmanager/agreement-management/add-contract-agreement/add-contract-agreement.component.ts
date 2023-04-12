import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ContractTypeStatus } from '../../shared/constants/contract-type.constants';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, Observable } from 'rxjs';
import { ContractType } from '../../models/contractType';
import { ContractmanangerService } from '../../contractmananger.service';
import { ContractCategory } from '../../models/contract-category';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Team } from '../../models/team';
import { ContractAgreement } from '../../models/contractAgreement';
import { AgreementStatus, DisplayMode } from '../../shared/constants/contract-manager.constants';
import { IUser, User } from '../../../admin/user-management/user-management.model';
import { AccountService } from '../../../core/auth/account.service';
import { Roles } from '../../shared/constants/user-role.constans';
import { SignatoryManagementComponent } from '../../shared/signatory-management/signatory-management.component';
import { ContractTemplate } from '../../models/contractTemplate';
import { numberValidator } from '../../../shared/validators/number.validator';
import { urlValidator } from '../../../shared/validators/url.validator';
import { ContractAgreementRevision } from '../../models/contractAgreementRevision';
import { RequestApprovalDialogComponent } from '../../shared/request-approval-dialog/request-approval-dialog.component';
import { ApproveDialogComponent } from '../../shared/approve-dialog/approve-dialog.component';
import { ApprovalCriteria } from '../../models/approval-criteria';
import { Location } from '@angular/common';
import { ConfirmationDialogComponent } from '../../shared/confirmation-dialog/confirmation-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { HttpStatusCode } from '@angular/common/http';
import { EntityStatus } from '../../shared/constants/entity-status';
import { NotificationConfig, NotificationMap } from '../../models/notifications-config';
import { UserGroup } from '../../models/user-group';

@Component({
  selector: 'jhi-add-contract-agreement',
  templateUrl: './add-contract-agreement.component.html',
  styleUrls: ['./add-contract-agreement.component.scss'],
})
export class AddContractAgreementComponent implements OnInit {
  pageLoading = false;
  contractAgreement: ContractAgreement = new ContractAgreement();
  historyColumns = ['events', 'createdBy', 'createdOn'];
  assignedUsers: any[] = [];
  currentUser: IUser = new User();
  team: Team | undefined;
  // teamFields: string[] = ['primaryOwner', 'secondaryOwner', 'approver'];
  teamFields: string[] = [Roles.SecondaryOwner, Roles.Approver, Roles.Observer, Roles.ExternalSignatory];
  agreementPdfSrc = '';
  templateDocSrc = '';
  attributeDetailsRows: FormArray = this.fb.array([]);
  attributeDetailsFormGroup: FormGroup = this.fb.group({ attributeDetailsFormArray: this.attributeDetailsRows });

  contractTypesByCategoryObserver: Observable<ContractType[]> | undefined;
  contractTypesByCategory: any[] = [];
  contractCategoriesObserver: Observable<ContractCategory[]> | undefined;
  contractCategories: ContractCategory[] = [];
  selectedContractType: any = new Object({ templates: [] });
  contractAgreementVersions: ContractAgreement[] | undefined;
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
    externalSignatory: [],
  });

  teamGroupForm = this.fb.group({
    secondaryOwner: [],
    contractAdmin: [],
    approver: [],
    observer: [],
    externalSignatory: [],
  });

  contractAgreementForm = this.fb.group({
    id: [null],
    contractCategory: ['', [Validators.required]],
    contractType: ['', [Validators.required]],
    agreementName: ['', [Validators.required, Validators.minLength(3)]],
    // agreementName: [''],
    agreementDescription: [''],
    agreementCode: [],
    // templateName: ['', [Validators.required, Validators.minLength(3)]],
    template: [{}],
    agreementStatus: 'DRAFT',
    genNewDocToUpdate: false,
    // attributes: this.fb.array([]),
    attributes: this.attributeDetailsRows,
    team: this.teamForm,
    // team: this.fb.group({
    //   primaryOwner: ['', [Validators.required, Validators.email]],
    //   secondaryOwner: ['',Validators.email],
    //   contractAdmin: ['',Validators.email],
    //   approver:['',[Validators.required, Validators.email]],
    //   observer: ['',Validators.email],
    //   externalReviewer: ['',Validators.email]
    // })
    teamGroups: this.teamGroupForm,
    notificationConfig: this.notificationConfig,
  });
  displayMode = '';
  // isEditMode = false;
  // isLinear = true;

  templateTableFormGroup = new FormGroup({
    templateTableFormArray: this.fb.array([]),
  });
  contractTypeDataSourceByCategory = new BehaviorSubject<AbstractControl[]>([]);
  templateTableColumns: string[] = ['templateName', 'add'];

  get contractAttributes(): FormArray {
    return <FormArray>this.contractAgreementForm.get('attributes');
  }
  get agreementName(): AbstractControl {
    return <AbstractControl>this.contractAgreementForm.get('agreementName');
  }
  get selectedTemplate(): AbstractControl {
    return <AbstractControl>this.contractAgreementForm.get('template');
  }
  get selectedContractTypeName(): AbstractControl {
    return <AbstractControl>this.contractAgreementForm.get('contractType');
  }
  get contractCategory(): AbstractControl {
    return <AbstractControl>this.contractAgreementForm.get('contractCategory');
  }

  get approvers(): FormArray {
    return <FormArray>this.teamForm.get('approvers');
  }

  get entityRole(): string {
    return this.contractAgreement.entityRole !== Roles.SecondaryOwner ? this.contractAgreement.entityRole : Roles.PrimaryOwner;
  }

  // agreementForm = this.fb.group({
  //   // contractType: [ContractTypeConstant.AGREEMENT],
  //   contractCategory: [null, Validators.required],
  //   contractTypeName: [null, Validators.required],
  //   agreementName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
  //   agreementCode: [],
  //   agreementDescription: [''],
  //
  //   // attributes: [this.selectedAttributes],
  //
  //
  //   // team: this.teamForm,
  // });
  @ViewChild('teamDialog') teamDialog: TemplateRef<any> | undefined;
  @ViewChild('viewAgreement') viewAgreement: TemplateRef<any> | undefined;
  @ViewChild('viewTemplateDoc') viewTemplateDoc: TemplateRef<any> | undefined;
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
    const agreementId = paramMap.get('agreementId');

    if (agreementId) {
      this.initViewMode(agreementId);
    } else {
      this.displayMode = DisplayMode.CREATE;
      this.contractAgreement = new ContractAgreement();
      // this.contractAgreementForm.patchValue({ templateName: 'Offer Letter Template' });
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
  attributeDetails(): FormArray {
    return this.attributeDetailsFormGroup.get('attributeDetailsFormArray') as FormArray;
  }
  attributes(): FormArray {
    return this.contractAgreementForm.get('attributes') as FormArray;
  }
  onCategorySelect(selectedCategory: any): void {
    this.getContractTypesByCategory(selectedCategory.id).subscribe(data1 => {
      data1.forEach(contractType => {
        this.contractTypesByCategory.push(contractType);
      });
    });
  }
  openTeamManagementDialog(): void {
    this.dialog
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      .open(this.teamDialog, {
        width: '70%',
        minHeight: '90%',
      })
      .afterClosed()
      .subscribe(data => {
        this.createAssignedUserList();
      });
  }

  getContractTypesByCategory(selectedCategoryId: string | undefined): Observable<ContractType[]> {
    this.resetAttributeDetails();
    this.contractTypesByCategoryObserver = this.contractmanagerService.contractTypesByCategory;
    this.contractmanagerService.getContractTypesByCategory(selectedCategoryId);
    return this.contractTypesByCategoryObserver;
  }
  resetAttributeDetails(): void {
    this.attributeDetailsRows.clear();
    // this.attributeDetailsRows= this.fb.array([]);
    // this.attributeDetailsFormGroup = this.fb.group({ 'attributeDetailsFormArray': this.attributeDetailsRows });
  }
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  addTemplate(row: any, isToggleOn: boolean): void {}

  onContractTypeSelect(selectedContract_Type: any): void {
    this.resetAttributeDetails();
    for (let i = 0; i < this.contractTypesByCategory.length; i++) {
      if (this.contractTypesByCategory[i].id === selectedContract_Type.id) {
        this.selectedContractType = this.contractTypesByCategory[i];
        break;
      }
      this.contractAgreement.contractType = this.selectedContractType;
    }

    if (this.displayMode === DisplayMode.UPDATE) {
      this.populateAttributeDetails();
    }
  }

  populateAttributeDetails(): void {
    if (this.attributeDetailsRows.length > 0) {
      return;
    }
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type,@typescript-eslint/no-empty-function
    function getValidatorsCompose(dataType: string, attributeOptions: { isMandatory: any; isHidden: any; isEditable: any }) {
      let validatorsCompose = Validators.compose([]);
      if (dataType === 'string') {
        if (attributeOptions.isMandatory) {
          validatorsCompose = Validators.compose([Validators.required]);
        }
      } else if (dataType === 'email') {
        if (attributeOptions.isMandatory) {
          validatorsCompose = Validators.compose([Validators.required, Validators.email]);
        } else {
          validatorsCompose = Validators.compose([Validators.email]);
        }
      } else if (dataType === 'number') {
        if (attributeOptions.isMandatory) {
          validatorsCompose = Validators.compose([Validators.required, numberValidator()]);
        } else {
          validatorsCompose = Validators.compose([numberValidator()]);
        }
      } else if (dataType === 'url') {
        if (attributeOptions.isMandatory) {
          validatorsCompose = Validators.compose([Validators.required, urlValidator()]);
        } else {
          validatorsCompose = Validators.compose([urlValidator()]);
        }
      }
      return validatorsCompose;
    }

    this.selectedContractType.attributes.forEach(
      (attributeConfig: {
        attributeName: string;
        displayName: string;
        dataType: string;
        helpMessage: string;
        attributeOptions: { isMandatory: any; isHidden: any; isEditable: any };
      }) => {
        const tempAttributeName = attributeConfig.attributeName;
        const tempDisplayName = attributeConfig.displayName;
        let tempAttributeValue = '';
        this.contractAgreement.attributes.forEach(attribute => {
          if (attribute.attributeName === attributeConfig.attributeName) {
            tempAttributeValue = attribute.attributeValue;
          }
        });
        let tempAttributeDetailsRow;
        const tempMasterData = 'no';
        // if (attributeMeta[tempAttributeName].Masterdata) {
        //   tempMasterData = attributeMeta[tempAttributeName].Masterdata
        //     ? attributeMeta[tempAttributeName].Masterdata.toLowerCase()
        //     : attributeMeta[tempAttributeName].Masterdata;
        // }

        const validatorsCompose = getValidatorsCompose(attributeConfig.dataType, attributeConfig.attributeOptions);

        if (validatorsCompose != null) {
          tempAttributeDetailsRow = this.fb.group({
            attributeName: tempAttributeName,
            displayName: tempDisplayName,
            attributeValue: [tempAttributeValue, validatorsCompose],
            masterData: tempMasterData,
            helpText: attributeConfig.helpMessage,
            isHidden: attributeConfig.attributeOptions.isHidden,
          });
        } else {
          tempAttributeDetailsRow = this.fb.group({
            attributeName: tempAttributeName,
            displayName: tempDisplayName,
            attributeValue: tempAttributeValue,
            masterData: tempMasterData,
            helpText: attributeConfig.helpMessage,
            isHidden: attributeConfig.attributeOptions.isHidden,
          });
        }
        if (this.displayMode === DisplayMode.UPDATE) {
          tempAttributeDetailsRow.markAllAsTouched();
        }
        if (!attributeConfig.attributeOptions.isEditable) {
          tempAttributeDetailsRow.disable();
        }

        // tempAttributeDetailsRow = this.fb.group({
        //    attributeName: tempAttributeName,
        //    attributeValue: [''],
        //    masterData: tempMasterData,
        //    helpText: 'help text',
        //    isHidden: tempIsHidden,
        //  });
        tempAttributeDetailsRow.valueChanges.subscribe(() => {
          this.onAttributeValueChanges();
        });
        this.attributeDetailsRows.push(tempAttributeDetailsRow);
      }
    );

    // eslint-disable-next-line no-console
    console.log('Inside Attribute fill .....');
  }

  // private syncTemplateSelection(): void {
  //   const templateTableCtrl = this.templateTableFormGroup.get('templateTableFormArray') as FormArray;
  //   const templateCtrl = this.contractAgreementForm.get('templateNames') as FormArray;
  //   for (let i = 0; i < templateTableCtrl.controls.length; i++) {
  //     if (templateTableCtrl.controls[i].value.add === true) {
  //       templateCtrl.value.push(templateTableCtrl.controls[i].value.templateName);
  //     }
  //   }
  // }
  saveAgreement(): void {
    // // this.syncAttributeDetails();
    // const tempContractAgreementForm =  new Object(this.contractAgreementForm.value);
    // // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // // @ts-ignore
    // tempContractAgreementForm.agreementStatus = 'DRAFT';
    // // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // // @ts-ignore
    // tempContractAgreementForm.template = new ContractTemplate();
    // // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // // @ts-ignore
    // tempContractAgreementForm.contractType = new ContractType();
    // // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // // @ts-ignore
    // tempContractAgreementForm.template.id = this.contractAgreementForm.value.template.id;
    // // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // // @ts-ignore
    // tempContractAgreementForm.contractType.id = this.contractAgreementForm.value.contractType.id
    this.contractAgreementForm.patchValue({ agreementStatus: 'DRAFT' });
    this.contractmanagerService.updateContractAgreement(this.contractAgreementForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.ngOnInit();
    });
  }
  rollBack(contractAgreement: ContractAgreement): void {
    const tempContractAgreement = new ContractAgreement();
    tempContractAgreement.id = contractAgreement.id;
    tempContractAgreement.version = contractAgreement.version;
    this.contractmanagerService.rollBackAgreement(tempContractAgreement).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.router.navigate(['/contractmanager/agreement/add-contract-agreement/' + contractAgreement.id]).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }
  // syncAttributeDetails(): void {
  //   const attributesCtrl = this.contractAgreementForm.get('attributes') as FormArray;
  //   for (let i = 0; i < this.attributeDetailsRows.length; i++) {
  //     attributesCtrl.setControl(i, this.attributeDetailsRows.controls[i]);
  //   }
  // }
  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }
  submitAgreement(): void {
    // this.syncAttributeDetails();
    // this.syncTemplateSelection();
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    this.contractmanagerService.createAgreementFiles(this.contractAgreementForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/agreement/view-contract-agreements']).then(() => {
        window.location.reload();
      });
    });
    // if (!this.isDraftEditActive && !this.isVersionEditActive) {
    //   this.contractAgreementForm.patchValue({ version: 0 });
    //   this.addContractAgreement();
    // } else if (this.isDraftEditActive) {
    //   this.contractAgreementForm.patchValue({ version: 0 });
    //   this.updateContractAgreement();
    // } else if (this.isVersionEditActive) {
    //   const currentVersion = this.contractAgreementForm.get('version').value;
    //   this.contractAgreementForm.patchValue({ version: currentVersion + 1 });
    //   this.updateContractAgreement();
    // }
  }

  setSelectedTemplate(_template: any): void {
    this.contractmanagerService.getContractTemplate(_template.id).subscribe((ct: ContractTemplate) => {
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.templateDocSrc = '/content/' + ct.tempFileName + '.docx';
      this.contractAgreementForm.patchValue({ template: _template });
      this.contractAgreementForm.patchValue({ genNewDocToUpdate: true });
    });
  }
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  getAgreementStatusFlag(): string {
    switch (this.contractAgreement.agreementStatus) {
      case AgreementStatus.DRAFT:
        return 'draft';

      // case AgreementStatus.CREATED:
      //   return 'created';

      case AgreementStatus.APPROVAL_PENDING:
        return 'approval-pending';

      case AgreementStatus.APPROVED:
        return 'approved';

      case AgreementStatus.APPROVAL_REJECTED:
        return 'approval-rejected';

      case AgreementStatus.SENT_FOR_SIGNATURE:
        return 'send-for-signature';

      case AgreementStatus.SIGNER_REJECTED:
        return 'signer-rejected';

      case AgreementStatus.COMPLETED:
        return 'completed';

      case AgreementStatus.CANCELED:
        return 'canceled';

      case AgreementStatus.EXPIRED:
        return 'expired';

      case AgreementStatus.REJECTED:
        return 'approval-rejected';
    }
  }

  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  getDisplayStatus(): string {
    switch (this.contractAgreement.agreementStatus) {
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
  // changeToEditMode(): void {
  //   this.isEditMode = false;
  //   this.isLinear = true;
  // }
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  discardAgreement(): void {}
  sendForApproval(): void {
    this.contractmanagerService.publishAgreement(this.contractAgreement).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/agreement/view-contract-agreements']).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }

  approveAgreement(): void {
    this.contractmanagerService.approveAgreement(this.contractAgreement).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['/contractmanager/agreement/view-contract-agreements']).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }
  sendForSignature(): void {
    //
  }
  openSignatoryDialog(): void {
    const dialogRef = this.dialog.open(SignatoryManagementComponent, {
      minWidth: '600px',
      minHeight: '500px',
      data: {
        selectedAgreementId: this.contractAgreement.id,
        signatories: this.contractAgreement.team.externalSignatory,
      },
    });
  }
  changeDisplayMode(display_Mode: string): void {
    this.displayMode = display_Mode;
    if (this.displayMode === DisplayMode.UPDATE) {
      this.contractCategoriesObserver = this.contractmanagerService.contractCategories;
      this.contractmanagerService.getContractCategories();
      this.contractCategoriesObserver.subscribe(data => {
        data.forEach(contractCategory => {
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          this.contractCategories[contractCategory.categoryName] = contractCategory;
          if (contractCategory.id === this.contractAgreement.contractCategory?.id) {
            this.getContractTypesByCategory(contractCategory.id).subscribe(data1 => {
              data1.forEach(contractType => {
                this.contractTypesByCategory.push(contractType);
                this.onContractTypeSelect(this.contractAgreement.contractType);
              });
            });
          }
        });
        // eslint-disable-next-line no-console
        console.log(data);
      });

      // this.attributeDetailsFormGroup.valueChanges.subscribe(() => {
      //   this.contractAgreementForm.patchValue({ genNewDocToUpdate: true });
      // })

      // eslint-disable-next-line guard-for-in
      //   for (let i = 0; i < this.attributeDetailsRows.length; i++) {
      //     this.attributeDetailsRows.controls[i].get('attributeValue')?.valueChanges.subscribe(() => {
      //       this.contractAgreementForm.patchValue({ genNewDocToUpdate: true });
      //     })
      //   }

      // const attributesCtrl = this.contractAgreementForm.get('attributes') as FormArray;
      // for (let i = 0; i < attributesCtrl.length; i++) {
      //   // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //   // @ts-ignore
      //   attributesCtrl[i].get('attributeValue')?.valueChanges.subscribe(() => {
      //     this.contractAgreementForm.patchValue({ genNewDocToUpdate: true });
      //   })
      // }

      this.contractAgreementForm.markAllAsTouched();
    }
  }
  openViewAgreementDialog(): void {
    // this.agreementPdfSrc = '/content/temp_agreement.pdf';
    this.dialog
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      .open(this.viewAgreement, {
        width: '100%',
        height: '100%',
        // top: '500px'
      })
      .afterClosed()
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      .subscribe(data => {});
  }
  openViewTemplateDialog(): void {
    // this.templatePdfSrc = "/content/ContractTemplate.docx";
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
  getContractAgreementRevisions(agreementId: string): void {
    this.contractmanagerService.getAgreementVersions(agreementId).subscribe((data: ContractAgreementRevision) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (!data || !data.contractAgreementRevisions) {
        // this.contractAgreementVersions = [];
        return;
      }
      this.contractAgreementVersions = data.contractAgreementRevisions;
    });
  }

  openRequestApprovalDialog(): void {
    this.dialog
      .open(RequestApprovalDialogComponent, {
        width: '30%',
        disableClose: true,
        data: this.contractAgreement.team.approver,
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.sendForApproval();
        }
      });
  }

  rejectContractAgreement(): void {
    this.dialog
      .open(ApproveDialogComponent, {
        width: '30%',
        disableClose: true,
        data: {
          isApprovalDialog: false,
        },
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.contractmanagerService.rejectAgreement(this.contractAgreement).subscribe(response => {
            if (response.status === HttpStatusCode.Ok) {
              this.updateStatus(EntityStatus.REJECTED);
              window.location.reload();
            }
          });
        }
      });
  }

  openApproveDialog(): void {
    this.dialog
      .open(ApproveDialogComponent, {
        width: '30%',
        disableClose: true,
        data: {
          isApprovalDialog: true,
        },
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.approveAgreement();
        }
      });
  }

  // deleteMember(id: User, role: string){
  //   const team = this.teamForm[role] as IUser[];
  //   const index = team.indexOf(id);
  //   if(index > 0){
  //     team.splice(index, 1);
  //   }
  // }

  updateStatus(status: string): void {
    const approvalStatus = this.contractAgreement.approvalStatus;
    const approvalCriteria = approvalStatus?.approvalCriteria;

    switch (status) {
      case AgreementStatus.APPROVAL_PENDING:
        this.contractAgreement.agreementStatus = AgreementStatus.APPROVAL_PENDING;

        if (
          approvalCriteria === ApprovalCriteria.All &&
          approvalStatus?.userApprovalStatus?.filter(x => x.userId === this.currentUser.id && x.hasApproved)?.length
        ) {
          this.contractAgreement.agreementStatus = ContractTypeStatus.WAITING_FOR_OTHERS_APPROVAL;
        }
        break;

      case AgreementStatus.REJECTED:
        this.contractAgreement.agreementStatus = AgreementStatus.REJECTED;
        break;

      case AgreementStatus.DRAFT:
        this.contractAgreement.agreementStatus = AgreementStatus.DRAFT;
        break;

      case AgreementStatus.APPROVED:
        this.contractAgreement.agreementStatus = AgreementStatus.APPROVED;
        break;

      case AgreementStatus.SENT_FOR_SIGNATURE:
        this.contractAgreement.agreementStatus = AgreementStatus.SENT_FOR_SIGNATURE;
        break;

      default:
        this.contractAgreement.agreementStatus = AgreementStatus.ERROR;
    }
  }

  deleteMember(user: any): void {
    if (!user.role) {
      return;
    }

    if (user.user.type === 'user') {
      const team = this.teamForm.get(user.role)?.value as IUser[];
      const index = team.findIndex(x => x.id === user.user.id);

      if (index >= 0) {
        team.splice(index, 1);
      }
    } else {
      const teamGroup = this.teamGroupForm.get(user.role)?.value as UserGroup[];
      const index = teamGroup.findIndex(x => x.id === user.user.id);

      if (index >= 0) {
        teamGroup.splice(index, 1);
      }
    }
  }

  navigateBack(): void {
    this.location.back();
  }

  deleteEntity(): void {
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

        if (this.contractAgreement.id && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.contractmanagerService.deleteAgreement(this.contractAgreement.id).subscribe(result => {
            if (result) {
              this.showConfirmationMessage('Contract Agreement Deleted Successfully');
              this.router.navigate(['/contractmanager/agreement/view-contract-agreements']);
            }
          });
        }
      });
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

  private createAssignedUserList(): void {
    this.assignedUsers = [];

    // this.teamFields.forEach(role => {
    //   const members = this.teamForm.value[role] as IUser[];
    //   if(members.length > 0){
    //     members.forEach((user) => {
    //       this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role, id: user.id });
    //     })
    //   }
    // });

    if (this.teamForm.value?.primaryOwner) {
      const primaryOwner = this.teamForm.value?.primaryOwner;
      this.assignedUsers.push({ firstName: primaryOwner.firstName, lastName: primaryOwner.lastName, role: Roles.PrimaryOwner });
    }
    if (this.teamForm.value?.approver) {
      const approver = this.teamForm.value?.approver;

      approver.forEach((user: { firstName: any; lastName: any; id: any }) => {
        this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: Roles.Approver, id: user.id, type: 'user' });
      });
    }
    if (this.teamForm.value?.secondaryOwner) {
      const secondaryOwner = this.teamForm.value?.secondaryOwner;

      secondaryOwner.forEach((user: { firstName: any; lastName: any; id: any }) => {
        this.assignedUsers.push({
          firstName: user.firstName,
          lastName: user.lastName,
          role: Roles.SecondaryOwner,
          id: user.id,
          type: 'user',
        });
      });
    }

    if (this.teamForm.value?.observer) {
      const observer = this.teamForm.value?.observer;

      observer.forEach((user: { firstName: any; lastName: any; id: any }) => {
        this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: Roles.Observer, id: user.id, type: 'user' });
      });
    }

    if (this.teamForm.value?.externalSignatory) {
      const externalSignatory = this.teamForm.value?.externalSignatory;

      externalSignatory.forEach((user: { firstName: any; lastName: any; id: any }) => {
        this.assignedUsers.push({
          firstName: user.firstName,
          lastName: user.lastName,
          role: Roles.ExternalSignatory,
          id: user.id,
          type: 'user',
        });
      });
    }

    Object.entries(this.teamGroupForm.value).forEach(prop => {
      if (prop[1] === null) {
        return;
      }

      const userGroup = prop[1] as UserGroup[];
      if (userGroup.length > 0) {
        userGroup.forEach(ug => {
          this.assignedUsers.push({
            firstName: ug.name,
            lastName: '',
            role: this.getPropertyReverse(prop[0]),
            id: ug.id,
            type: 'group',
            members: ug.members.length,
          });
        });
      }
    });

    this.assignNotificationConfig();
  }

  private assignNotificationConfig(): void {
    this.notificationConfig = new NotificationConfig();
    const notifications = this.notificationConfigForm.get('notifications') as FormArray;

    if (notifications.length > 0) {
      const configArr = notifications.value as { groupId: string; enabled: boolean }[];
      const temp: NotificationMap = {};
      configArr.forEach(config => {
        temp[config.groupId] = config.enabled;
        this.notificationConfig.userConfig = temp;
      });

      this.contractAgreementForm.patchValue({ notificationConfig: this.notificationConfig });
    } else if (this.contractAgreement.notificationConfig) {
      this.notificationConfig = this.contractAgreement.notificationConfig;
    }
  }

  private initViewMode(agreementId: string): void {
    this.pageLoading = true;
    this.displayMode = DisplayMode.VIEW;
    // this.isEditMode = true;
    // this.isLinear = false;

    this.contractmanagerService.getContractAgreement(agreementId).subscribe((ca: ContractAgreement) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (ca) {
        this.contractAgreement = ca;
        this.contractAgreementForm.patchValue({
          id: ca.id,
          contractCategory: ca.contractCategory,
          contractType: { id: ca.contractType?.id, name: ca.contractType?.name },
          agreementName: ca.agreementName,
          agreementDescription: ca.agreementDescription,
          agreementCode: ca.agreementCode,
          template: { id: ca.template?.id, templateName: ca.template?.templateName },
          agreementStatus: ca.agreementStatus,
          team: ca.team,
          teamGroups: ca.teamGroups,
        });
        // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
        this.agreementPdfSrc = '/content/' + ca.tempAgreementFile + '.pdf';
        // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
        this.templateDocSrc = '/content/' + ca.tempTemplateFile + '.docx';
        this.createAssignedUserList();
      }
      this.updateStatus(ca.agreementStatus ?? AgreementStatus.ERROR);
      this.pageLoading = false;
    });
    this.getContractAgreementRevisions(agreementId);
  }

  private onAttributeValueChanges(): void {
    this.contractAgreementForm.patchValue({ genNewDocToUpdate: true });
  }

  private getPropertyReverse(role?: string): Roles {
    switch (role) {
      case 'primaryOwner':
        return Roles.PrimaryOwner;

      case 'contractAdmin':
        return Roles.ContractAdmin;

      case 'contributor':
        return Roles.Contributor;

      case 'deviationApprover':
        return Roles.DeviationApprover;

      case 'externalReviewer':
        return Roles.ExternalReviewer;

      case 'externalSignatory':
        return Roles.ExternalSignatory;

      case 'internalSignatory':
        return Roles.InternalSignatory;

      case 'observer':
        return Roles.Observer;

      case 'reviewer':
        return Roles.Reviewer;

      case 'secondaryOwner':
        return Roles.SecondaryOwner;

      case 'approver':
        return Roles.Approver;

      default:
        return Roles.None;
    }
  }
}
