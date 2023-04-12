import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DisplayMode } from '../../../shared/constants/contract-manager.constants';
import { ContractRule } from '../../../models/contractRule';
import { ContractType } from '../../../models/contractType';
import { ContractCategory } from '../../../models/contract-category';
import { Observable } from 'rxjs';
import { ContractmanangerService } from '../../../contractmananger.service';

@Component({
  selector: 'jhi-add-contract-rule',
  templateUrl: './add-contract-rule.component.html',
  styleUrls: ['./add-contract-rule.component.scss'],
})
export class AddContractRuleComponent implements OnInit {
  contractRule: ContractRule = new ContractRule();
  displayMode = '';
  contractTypesByCategoryObserver: Observable<ContractType[]> | undefined;
  contractTypesByCategory: any[] = [];
  contractCategoriesObserver: Observable<ContractCategory[]> | undefined;
  contractCategories: ContractCategory[] = [];
  // ruleDetails: FormArray = this.fb.array([]);
  ruleDetailsFormGroup: FormGroup = this.fb.group({ ruleDetailsFormArray: this.fb.array([]) });
  ruleTypes = [
    'Approval Rule',
    'Clause Assembly',
    'Dynamic Association',
    'Dynamic Attribute',
    'Event Rule',
    'Extension Selection',
    'Mandatory Association',
    'Notification Rule',
    'Supersede Rule',
    'Template Selection',
  ];

  teamRoles = ['Approver', 'Clause Approver', 'Deviation Approver', 'External Signatory', 'Internal Signatory', 'Reviewer'];
  contractRuleForm = this.fb.group({
    id: [null],
    contractCategory: [new ContractCategory('', '')],
    contractType: [new ContractType()],
    ruleCategory: ['', [Validators.required, Validators.minLength(3)]],
    ruleDescription: [''],
    ruleType: [''],
    teamRole: [''],
    rules: this.ruleDetails,
  });
  get ruleCategory(): AbstractControl {
    return <AbstractControl>this.contractRuleForm.get('ruleCategory');
  }
  get ruleType(): AbstractControl {
    return <AbstractControl>this.contractRuleForm.get('ruleType');
  }

  constructor(private fb: FormBuilder, private contractmanagerService: ContractmanangerService, private route: ActivatedRoute) {
    //do nothing
  }

  ngOnInit(): void {
    const paramMap = this.route.snapshot.paramMap;
    const ruleId = paramMap.get('ruleId');
    if (ruleId) {
      this.initViewMode(ruleId);
    } else {
      this.displayMode = DisplayMode.CREATE;
      this.contractRule = new ContractRule();
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
  }
  ruleDetails(): FormArray {
    return this.ruleDetailsFormGroup.get('ruleDetailsFormArray') as FormArray;
  }
  getContractTypesByCategory(selectedCategoryId: string | undefined): Observable<ContractType[]> {
    this.contractTypesByCategoryObserver = this.contractmanagerService.contractTypesByCategory;
    this.contractmanagerService.getContractTypesByCategory(selectedCategoryId);
    return this.contractTypesByCategoryObserver;
  }
  onCategorySelect(selectedCategory: any): void {
    this.getContractTypesByCategory(selectedCategory.id).subscribe(data1 => {
      data1.forEach(contractType => {
        this.contractTypesByCategory.push(contractType);
      });
    });
  }
  onContractTypeSelect(selectedContract_Type: any): void {
    // this.resetAttributeDetails();
    // for (let i = 0; i < this.contractTypesByCategory.length; i++) {
    //   if (this.contractTypesByCategory[i].id === selectedContract_Type.id) {
    //     this.selectedContractType = this.contractTypesByCategory[i];
    //     break;
    //   }
    // }
    // this.selectedContractType.attributes.forEach((attributeConfig: { attributeName: string; attributeOptions: { isMandatory: any; isHidden: any; isEditable: any; }; helpMessage: any; }) => {
    //   const tempAttributeName = attributeConfig.attributeName;
    //   let tempAttributeValue = '';
    //   this.contractAgreement.attributes.forEach(attribute => {
    //     if (attribute.attributeName === attributeConfig.attributeName){
    //       tempAttributeValue = attribute.attributeValue;
    //     }
    //   })
    //   let tempAttributeDetailsRow;
    //   const tempMasterData = 'no';
    //   // if (attributeMeta[tempAttributeName].Masterdata) {
    //   //   tempMasterData = attributeMeta[tempAttributeName].Masterdata
    //   //     ? attributeMeta[tempAttributeName].Masterdata.toLowerCase()
    //   //     : attributeMeta[tempAttributeName].Masterdata;
    //   // }
    //
    //
    //   if (attributeConfig.attributeOptions.isMandatory) {
    //     tempAttributeDetailsRow = this.fb.group({
    //       attributeName: tempAttributeName,
    //       attributeValue: [tempAttributeValue, [Validators.required]],
    //       masterData: tempMasterData,
    //       helpText: attributeConfig.helpMessage,
    //       isHidden: attributeConfig.attributeOptions.isHidden,
    //     });
    //   } else {
    //     tempAttributeDetailsRow = this.fb.group({
    //       attributeName: tempAttributeName,
    //       attributeValue: tempAttributeValue,
    //       masterData: tempMasterData,
    //       helpText: attributeConfig.helpMessage,
    //       isHidden: attributeConfig.attributeOptions.isHidden,
    //     });
    //   }
    //
    //   if (!attributeConfig.attributeOptions.isEditable) {
    //     tempAttributeDetailsRow.disable();
    //   }
    //
    //   this.attributeDetailsRows.push(tempAttributeDetailsRow);
    // });
  }
  addNewRule(): void {
    this.ruleDetails().push(
      this.fb.group({
        ruleName: '',
      })
    );
  }
  changeDisplayMode(display_Mode: string): void {
    this.displayMode = display_Mode;
  }
  compareObjects(object1: any, object2: any): boolean {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return object1 && object2 && object1.id === object2.id;
  }
  private initViewMode(agreementId: string): void {
    this.displayMode = DisplayMode.VIEW;

    // this.contractmanagerService.getContractAgreement(agreementId).subscribe((ca: ContractAgreement) => {
    //   // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    //   if (ca) {
    //     this.contractAgreement = ca;
    //     this.contractAgreementForm.patchValue({
    //       id: ca.id,
    //       contractCategory: ca.contractCategory,
    //       contractType: ca.contractType,
    //       agreementName : ca.agreementName,
    //       agreementDescription: ca.agreementDescription,
    //       agreementCode: ca.agreementCode,
    //       template: ca.template,
    //       agreementStatus: ca.agreementStatus,
    //       team: ca.team,
    //     });
    //     this.createAssignedUserList();
    //   }
    // });
  }
}
