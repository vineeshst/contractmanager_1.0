import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable, Subscription, zip } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ContractCategory } from '../../../models/contract-category';
import { ContractType } from '../../../models/contractType';
import { ContractmanangerService } from '../../../contractmananger.service';
import { AssociationDialogComponent } from './association-dialog/association-dialog.component';
import { AttributeDialogComponent } from './attribute-dialog/attribute-dialog.component';
import { Association } from '../../../models/association';
import { ContractTypeConstant } from '../../../shared/constants/contract-type.constants';
import { AttributeConfig } from '../../../models/attributeConfig';
import { Team } from '../../../models/team';
import { ConfigurationService } from '../../configuration.service';
import { Roles } from '../../../shared/constants/user-role.constans';
import { AccountService } from '../../../../core/auth/account.service';
import { ContractTypeRevision } from '../../../models/contract-type-revision';
import { IUser, User } from '../../../../admin/user-management/user-management.model';
import { UserManagementService } from '../../../user-management/user-management.service';
import { AddContractCategoryComponent } from '../../add-contract-category/add-contract-category.component';
import * as fileSaver from 'file-saver';
import { ApprovalCriteria } from '../../../models/approval-criteria';
import { UserApprovalStatus } from '../../../models/user-spproval-status';
import { RequestApprovalDialogComponent } from '../../../shared/request-approval-dialog/request-approval-dialog.component';
import { ApproveDialogComponent } from '../../../shared/approve-dialog/approve-dialog.component';
import { EntityStatus } from '../../../shared/constants/entity-status';
import { UserGroup } from '../../../models/user-group';
import { ConfirmationDialogComponent } from '../../../shared/confirmation-dialog/confirmation-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { NotificationConfig } from '../../../models/notifications-config';

@Component({
  selector: 'jhi-add-contract-type',
  templateUrl: './add-contract-type.component.html',
  styleUrls: ['./add-contract-type.component.scss'],
})
export class AddContractTypeComponent implements OnInit {
  // fileSaver = require('file-saver');
  pageLoading = false;
  contractType: ContractType | undefined;

  historyColumns = ['events', 'createdBy', 'createdOn'];

  isEditMode = false;
  isLinear = true;

  currentUser: IUser = new User();

  selectedAttributeSearchControl = new FormControl();
  globalAttributeSearchControl = new FormControl();
  dpTileSelectedAttrSearchControl = new FormControl();
  dpTileGlobalAttrSearchControl = new FormControl();
  dpListSelectedAttrSearchControl = new FormControl();
  dpListGlobalAttrSearchControl = new FormControl();

  selectedAttributeSearch: string | undefined;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  globalAttributeSearch: string;
  dpTileSelectedAttrSearch: string | undefined;
  dpTileGlobalAttrSearch: string | undefined;
  dpListSelectedAttrSearch: string | undefined;
  dpListGlobalAttrSearch: string | undefined;

  dpListAttributes: AttributeConfig[] = [];
  dpTileAttributes: AttributeConfig[] = [];
  attributeConfigs: AttributeConfig[] = [];
  selectedAttributes: AttributeConfig[] = [];
  selectedTileAttributes: AttributeConfig[] | undefined = [];
  selectedListAttributes: AttributeConfig[] | undefined = [];
  selectedAttribute: AttributeConfig | undefined;
  selectedTileAttribute: AttributeConfig | undefined;
  selectedListAttribute: AttributeConfig | undefined;

  contractCategories: Observable<ContractCategory[]> | undefined;
  contractTypesByCategory: Observable<ContractType[]> | undefined;
  contractTypeVersions: ContractType[] | undefined;
  contractTypesByCategoryList: ContractType[] | undefined;
  contractTypeDataSourceByCategory = new BehaviorSubject<AbstractControl[]>([]);

  assignedUsers: any[] = [];
  team: Team | undefined;
  teamFields: string[] = [Roles.SecondaryOwner, Roles.Approver, Roles.Observer];
  associationTableFormGroup = new FormGroup({
    associationTableFormArray: this.fb.array([]),
  });

  notificationConfig: NotificationConfig = new NotificationConfig();
  notificationConfigForm: FormGroup = this.fb.group({
    notifications: this.fb.array([]),
  });

  associationsForm = this.fb.group({});

  teamForm = this.fb.group({
    primaryOwner: [],
    secondaryOwner: [],
    contractAdmin: [],
    approver: [],
    observer: [],
    externalReviewer: [],
  });

  teamGroupForm = this.fb.group({
    secondaryOwner: [],
    contractAdmin: [],
    approver: [],
    observer: [],
    externalReviewer: [],
  });

  contractTypeForm = this.fb.group({
    contractType: [ContractTypeConstant.AGREEMENT],
    contractTypeName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    contractCategory: [null, Validators.required],
    contractTypeCode: [],
    description: ['', Validators.maxLength(1000)],
    allowThirdPartyPaper: [true],
    allowClauseAssembly: [false],
    qrCode: [false],
    allowCopyWithAssociations: [false],
    twoColumnAttributeLayout: [false],
    enableCollaboration: [false],
    enableAutoSupersede: [false],
    expandDropdownOnMouseHover: [false],
    status: [EntityStatus.DRAFT],

    attributes: [this.selectedAttributes],
    // associations: this.associationsForm,

    displayPreference: [
      {
        tileAttributes: this.selectedTileAttributes,
        listAttributes: this.selectedListAttributes,
      },
    ],
    team: this.teamForm,
    teamGroups: this.teamGroupForm,
    createdBy: [''],
    createdOn: [''],
    updatedBy: [''],
    version: [1],
  });

  get contractTypeName(): AbstractControl {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return this.contractTypeForm.get('contractTypeName');
  }

  get description(): AbstractControl {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return this.contractTypeForm.get('description');
  }

  get entityRole(): string {
    return (this.contractType?.entityRole !== Roles.SecondaryOwner ? this.contractType?.entityRole : Roles.PrimaryOwner) ?? Roles.Observer;
  }

  get displayMode(): string {
    if (this.isLinear) {
      return 'CREATE';
    } else if (this.isEditMode) {
      return 'UPDATE';
    } else {
      return 'VIEW';
    }
  }

  set displayMode(mode: string) {
    if (mode === 'CREATE') {
      this.isEditMode = true;
      this.isLinear = true;
    }

    if (mode === 'UPDATE') {
      this.isEditMode = true;
      this.isLinear = false;
    }

    if (mode === 'VIEW') {
      this.isEditMode = false;
      this.isLinear = false;
    }
  }

  @ViewChild('teamDialog') teamDialog: TemplateRef<any> | undefined;

  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private contractmanangerService: ContractmanangerService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private configurationService: ConfigurationService,
    private location: Location,
    private router: Router,
    private accoutService: AccountService,
    private userManagementService: UserManagementService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const paramMap = this.route.snapshot.paramMap;
    const contractTypeId = paramMap.get('contractTypeId');

    if (contractTypeId) {
      this.initEditMode(contractTypeId);
    } else {
      this.contractType = new ContractType();
    }

    this.contractCategories = this.contractmanangerService.contractCategories;
    this.contractmanangerService.getContractCategories();
    this.contractmanangerService.getAttributesMeta().subscribe(attributesMetaData => {
      this.attributeConfigs = attributesMetaData.attributeConfigs ?? [];
    });

    this.accoutService.identity().subscribe(account => {
      this.currentUser = Object.assign(this.currentUser, account);
      this.teamForm.patchValue({
        primaryOwner: this.currentUser,
      });
      this.assignedUsers.push({ firstName: this.currentUser.firstName, lastName: this.currentUser.lastName, role: Roles.PrimaryOwner });
    });
    if (this.contractType) {
      this.updateStatus(this.contractType.status);
    }
  }

  onCategorySelect(selectedCategory: any): void {
    this.contractTypesByCategory = this.contractmanangerService.contractTypesByCategory;
    this.contractmanangerService.getContractTypesByCategory(selectedCategory);
  }

  categoryCompare(c1: string, c2: string): boolean {
    return c1 === c2;
  }

  clearSearchField(search: FormControl): void {
    search.setValue('');
  }

  selectAttribute(attribute: AttributeConfig, selectionType: string): void {
    switch (selectionType) {
      case 'Attribute':
        this.selectedAttribute = attribute;
        break;

      case 'dpTile':
        this.selectedTileAttribute = attribute;
        break;

      case 'dpList':
        this.selectedListAttribute = attribute;
        break;

      default:
        break;
    }
  }

  addAttribute(selectionType: string): void {
    let index: number;

    switch (selectionType) {
      case 'Attribute':
        index = this.attributeConfigs.indexOf(this.selectedAttribute!);
        if (index > -1) {
          this.attributeConfigs.splice(index, 1);
          this.selectedAttributes.push(this.selectedAttribute!);
        }
        break;

      case 'dpTile':
        index = this.dpTileAttributes.indexOf(this.selectedTileAttribute!);
        if (index > -1) {
          this.dpTileAttributes.splice(index, 1);
          this.selectedTileAttributes!.push(this.selectedTileAttribute!);
        }
        break;

      case 'dpList':
        index = this.dpListAttributes.indexOf(this.selectedListAttribute!);
        if (index > -1) {
          this.dpListAttributes.splice(index, 1);
          this.selectedListAttributes!.push(this.selectedListAttribute!);
        }
        break;

      default:
        break;
    }
  }

  removeAttribute(selectionType: string): void {
    let index: number;

    switch (selectionType) {
      case 'Attribute':
        index = this.selectedAttributes.indexOf(this.selectedAttribute!);
        if (index > -1) {
          this.selectedAttributes.splice(index, 1);
          this.attributeConfigs.push(this.selectedAttribute!);
        }
        break;

      case 'dpTile':
        index = this.selectedTileAttributes!.indexOf(this.selectedTileAttribute!);
        if (index > -1) {
          this.selectedTileAttributes!.splice(index, 1);
          this.dpTileAttributes.push(this.selectedTileAttribute!);
        }
        break;

      case 'dpList':
        index = this.selectedListAttributes!.indexOf(this.selectedListAttribute!);
        if (index > -1) {
          this.selectedListAttributes!.splice(index, 1);
          this.dpListAttributes.push(this.selectedListAttribute!);
        }
        break;

      default:
        break;
    }
  }

  prefillDpAttributes(): void {
    this.dpListAttributes = Object.assign([], this.selectedAttributes);
    this.dpTileAttributes = Object.assign([], this.selectedAttributes);
    this.selectedTileAttributes = this.selectedTileAttributes!.filter(x => this.dpTileAttributes.includes(x));
    this.selectedListAttributes = this.selectedListAttributes!.filter(x => this.dpListAttributes.includes(x));
  }

  save(): void {
    this.patchFormValues(false);
    this.contractmanangerService.addContractType(this.contractTypeForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.router.navigate(['contractmanager', 'configuration', 'view-contract-types']);
    });
  }

  showConfirmationMessage(message: string): void {
    this.snackBar.open(message, 'Ok', {
      duration: 2000,
    });
  }

  updateType(): void {
    this.patchFormValues(true);
    this.contractmanangerService.updateContractType(this.contractTypeForm.value).subscribe(response => {
      this.showConfirmationMessage(response.message);
      this.initEditMode(this.contractType!.id!.toString());
    });
  }

  openAssociationDialog(): void {
    this.dialog
      .open(AssociationDialogComponent, {
        width: '60%',
      })
      .afterClosed()
      .subscribe((data: Association) => {
        this.contractTypesByCategory!.pipe(
          tap(associations => {
            associations.push(new ContractType());
          })
        );
      });
  }

  openAttributeDialog(mode: string): void {
    this.dialog
      .open(AttributeDialogComponent, {
        width: '55%',
        data: {
          mode,
          attribute: this.selectedAttribute,
        },
      })
      .afterClosed()
      .subscribe((data: AttributeConfig) => {
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (!data) {
          return;
        }
        this.selectedAttribute = data;

        const index = this.selectedAttributes.findIndex(value => value.attributeName === data.attributeName);

        if (index > -1) {
          this.selectedAttributes[index] = data;
        } else {
          const remIndex = this.attributeConfigs.findIndex(value => value.attributeName === data.attributeName);

          if (remIndex > -1) {
            this.attributeConfigs.splice(remIndex, 1);
          }
          this.selectedAttributes.push(data);
        }
      });
  }
  openAddCategoryDialog(): void {
    this.dialog
      .open(AddContractCategoryComponent, {
        width: '40%',
      })
      .afterClosed()
      .subscribe(() => {
        this.contractmanangerService.getContractCategories();
      });
  }

  openTeamManagementDialog(mode: string): void {
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

  deleteMember(user: any): void {
    if (!user.role) {
      return;
    }

    if (user.type === 'user') {
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

  getContractTypeFlag(): string {
    switch (this.contractType?.status) {
      case EntityStatus.DRAFT:
        return 'draft';

      case EntityStatus.APPROVAL_PENDING:
        return 'waiting-for-approval';

      case EntityStatus.APPROVED:
        return 'approved';

      case EntityStatus.REJECTED:
        return 'rejected';

      default:
        return 'draft';
    }
  }

  sendForApproval(message: string): void {
    this.configurationService
      .updateContractTypeStatus(this.contractType!.id!, {
        status: EntityStatus.APPROVAL_PENDING,
        user: null,
        message,
      })
      .subscribe(
        response => {
          this.contractType!.status = response;
          // eslint-disable-next-line no-console
          console.log(response);
          //this.showConfirmationMessage(data);
        },
        error => {
          // eslint-disable-next-line no-console
          console.log(error);
          //this.showConfirmationMessage(error);
        }
      );
  }

  approveContractType(message: string): void {
    this.configurationService
      .updateContractTypeStatus(this.contractType!.id!, {
        status: EntityStatus.APPROVED,
        user: null,
        message,
      })
      .subscribe(
        response => {
          this.updateStatus(response);
          // eslint-disable-next-line no-console
          console.log(response);
          //this.showConfirmationMessage(data);
        },
        error => {
          // eslint-disable-next-line no-console
          console.log(error);
        }
      );
  }

  rejectContractType(): void {
    this.configurationService
      .updateContractTypeStatus(this.contractType!.id!, {
        status: EntityStatus.REJECTED,
        user: null,
      })
      .subscribe(
        response => {
          this.updateStatus(response);
        },
        error => {
          // eslint-disable-next-line no-console
          console.log(error);
        }
      );
  }

  navigateBack(): void {
    this.location.back();
  }

  getContractTypeRevisions(contractTypeId: string): void {
    this.configurationService.getContractTypeVersions(contractTypeId).subscribe((data: ContractTypeRevision) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (!data || !data.contractTypeRevisions) {
        return;
      }
      this.contractTypeVersions = data.contractTypeRevisions;
    });
  }

  downloadAttributes(): void {
    const attributesJson = { Attributes: {} };
    this.selectedAttributes.forEach(attribute => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      attributesJson.Attributes[attribute.attributeName + ''] = '';
    });
    const blob = new Blob([JSON.stringify(attributesJson)], { type: 'text/json; charset=utf-8' });
    fileSaver.saveAs(blob, 'attributes.json');
  }

  updateStatus(status: string): void {
    const approvalStatus = this.contractType?.approvalStatus;
    const approvalCriteria = approvalStatus?.approvalCriteria;

    switch (status) {
      case EntityStatus.APPROVAL_PENDING:
        this.contractType!.status = EntityStatus.APPROVAL_PENDING;

        if (
          approvalCriteria === ApprovalCriteria.All &&
          approvalStatus?.userApprovalStatus?.filter(x => x.userId === this.currentUser.id && x.hasApproved)?.length
        ) {
          this.contractType!.status = EntityStatus.WAITING_FOR_OTHERS_APPROVAL;
        }
        break;

      case EntityStatus.REJECTED:
        this.contractType!.status = EntityStatus.REJECTED;
        break;

      case EntityStatus.DRAFT:
        this.contractType!.status = EntityStatus.DRAFT;
        break;

      case EntityStatus.APPROVED:
        this.contractType!.status = EntityStatus.APPROVED;
        break;

      case EntityStatus.DELETED:
        this.router.navigate(['contractmanager/configuration/view-contract-types']);
        break;

      default:
        this.contractType!.status = EntityStatus.ERROR;
    }
  }

  rollBack(contractType: ContractType): void {
    const tempContractType = new ContractType();
    tempContractType.id = contractType.id;
    tempContractType.version = contractType.version;
    this.configurationService.rollBackContractType(tempContractType).subscribe(response => {
      this.showConfirmationMessage(response.message);
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      this.router.navigate(['/contractmanager/configuration/view-contract-type/agreement/' + contractType.id]).then(() => {
        window.location.reload();
      });
      // this.dialogRef.close();
    });
  }

  openRequestApprovalDialog(): void {
    this.dialog
      .open(RequestApprovalDialogComponent, {
        width: '30%',
        disableClose: true,
        data: this.contractType?.team?.approver,
      })
      .afterClosed()
      .subscribe(message => {
        if (message !== false) {
          this.sendForApproval(message);
        }
      });
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
          this.approveContractType(message);
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

  deleteContractType(): void {
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

        if (this.contractType!.id && (this.entityRole === Roles.PrimaryOwner || this.entityRole === Roles.SecondaryOwner)) {
          this.configurationService
            .updateContractTypeStatus(this.contractType!.id, {
              status: EntityStatus.DELETED,
              user: null,
            })
            .subscribe(response => {
              this.showConfirmationMessage('Contract Type Deleted Successfully');
              this.router.navigate(['/contractmanager/agreement/view-contract-agreements']);
            });
        }
      });
  }

  rejectEntity(): void {
    return;
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
    if (this.teamForm.value?.observer) {
      const observer = this.teamForm.value?.observer;
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      observer.forEach(user => {
        this.assignedUsers.push({ firstName: user.firstName, lastName: user.lastName, role: Roles.Observer, id: user.id });
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
            role: 'APPROVER',
            id: ug.id,
            type: 'group',
            members: ug.members.length,
          });
        });
      }
    });
  }

  private patchFormValues(isUpdateMode: boolean): void {
    this.contractTypeForm.patchValue({
      displayPreference: {
        tileAttributes: this.selectedTileAttributes,
        listAttributes: this.selectedListAttributes,
      },
      attributes: this.selectedAttributes,
    });
    this.contractTypeForm.patchValue(!isUpdateMode ? { createdBy: this.currentUser.firstName } : { updatedBy: this.currentUser.firstName });
  }
  private initEditMode(contractTypeId: string): void {
    this.pageLoading = true;
    this.isEditMode = false;
    this.isLinear = false;

    this.contractmanangerService.getContractType(contractTypeId).subscribe((ct: ContractType) => {
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      if (ct) {
        this.contractType = ct;
        this.contractTypeForm.patchValue({
          contractTypeName: ct.name,
          createdOn: ct.createdDate,
          createdBy: ct.createdBy,
          version: ct.version,
          contractCategory: ct.contractCategory!.id,
          description: ct.description,
          allowThirdPartyPaper: ct.allowThirdPartyPaper,
          allowClauseAssembly: ct.allowClauseAssembly,
          qrCode: ct.qrCode,
          allowCopyWithAssociations: ct.allowCopyWithAssociations,
          twoColumnAttributeLayout: ct.twoColumnAttributeLayout,
          enableCollaboration: ct.enableCollaboration,
          enableAutoSupersede: ct.enableAutoSupersede,
          expandDropdownOnMouseHover: ct.expandDropdownOnMouseHover,
        });
        this.selectedAttributes = ct.attributes ?? [];
        this.attributeConfigs = this.attributeConfigs.filter(x => !ct.attributes!.some(y => y.attributeName === x.attributeName));
        this.teamForm.patchValue(Object.assign({}, ct.team));
        this.teamGroupForm.patchValue(Object.assign({}, ct.teamGroups));
        if (ct.displayPreference) {
          this.selectedTileAttributes = ct.displayPreference.tileAttributes;
          this.selectedListAttributes = ct.displayPreference.listAttributes;
          this.dpTileAttributes = ct.attributes!.filter(x => !this.selectedTileAttributes!.some(y => y.attributeName === x.attributeName));
          this.dpListAttributes = ct.attributes!.filter(x => !this.selectedListAttributes!.some(y => y.attributeName === x.attributeName));
        }
        this.createAssignedUserList();
      }
      this.pageLoading = false;
      this.updateStatus(this.contractType!.status);
    });
    this.getContractTypeRevisions(contractTypeId);
  }
}
