import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { HttpClient, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { ApprovedRequest } from './models/approvedRequest';
import { ContractCategory } from './models/contract-category';
import { ContractType } from './models/contractType';
import { ContractAgreement } from '../contractmanager/models/contractAgreement';
import { ContractTemplate } from './models/contractTemplate';
import { map } from 'rxjs/operators';
import { SERVER_API_URL } from '../app.constants';
import { ApiService } from './models/apiService';
import { ContractClause } from './models/contractClause';
import { EntityStatus } from './shared/constants/entity-status';

@Injectable({
  providedIn: 'root',
})
export class ContractmanangerService {
  public resourceUrl = SERVER_API_URL + 'api';
  private _contractCategories: BehaviorSubject<ContractCategory[]>;
  private _contractTypesByCategory: BehaviorSubject<ContractType[]>;
  private _contractTypes: BehaviorSubject<ContractType[]>;
  private _contractAgreements: BehaviorSubject<ContractAgreement[]>;
  private _contractTemplates: BehaviorSubject<ContractTemplate[]>;
  private _contractClauses: BehaviorSubject<ContractClause[]>;
  private _contractTypeRevisions: BehaviorSubject<any[]>;
  private _contractAgreementRevisions: BehaviorSubject<any[]>;
  private _apiServices: BehaviorSubject<ApiService[]>;
  private _uploadedTemplate: BehaviorSubject<any>;
  private _uploadedClause: BehaviorSubject<any>;
  private dataStore: {
    contractCategories: ContractCategory[];
    contractTypesByCategory: ContractType[];
    contractTypes: ContractType[];
    contractAgreements: ContractAgreement[];
    apiServices: ApiService[];
    uploadedTemplate: any;
    uploadedClause: any;
    contractTemplates: ContractTemplate[];
    contractClauses: ContractClause[];
    contractTypeRevisions: any[];
    contractAgreementRevisions: any[];
    attributesMeta: any[];
  };
  constructor(private http: HttpClient) {
    this.dataStore = {
      contractCategories: [],
      contractTypesByCategory: [],
      contractTypes: [],
      contractAgreements: [],
      apiServices: [],
      uploadedTemplate: {},
      uploadedClause: {},
      contractTemplates: [],
      contractClauses: [],
      contractTypeRevisions: [],
      contractAgreementRevisions: [],
      attributesMeta: [],
    };
    this._contractCategories = new BehaviorSubject<ContractCategory[]>([]);
    this._contractTypesByCategory = new BehaviorSubject<ContractType[]>([]);
    this._contractTypes = new BehaviorSubject<ContractType[]>([]);
    this._contractAgreements = new BehaviorSubject<ContractAgreement[]>([]);
    this._contractTemplates = new BehaviorSubject<ContractTemplate[]>([]);
    this._contractClauses = new BehaviorSubject<ContractClause[]>([]);
    this._contractTypeRevisions = new BehaviorSubject<any[]>([]);
    this._contractAgreementRevisions = new BehaviorSubject<any[]>([]);
    this._apiServices = new BehaviorSubject<ApiService[]>([]);
    this._uploadedTemplate = new BehaviorSubject<string>('');
    this._uploadedClause = new BehaviorSubject<string>('');
  }
  get contractCategories(): Observable<ContractCategory[]> {
    return this._contractCategories.asObservable();
  }
  get contractTypesByCategory(): Observable<ContractType[]> {
    return this._contractTypesByCategory.asObservable();
  }
  get contractTypeRevisions(): Observable<ContractType[]> {
    return this._contractTypeRevisions.asObservable();
  }
  get contractAgreementRevisions(): Observable<ContractType[]> {
    return this._contractAgreementRevisions.asObservable();
  }
  get contractTypes(): Observable<ContractType[]> {
    return this._contractTypes.asObservable();
  }
  get contractAgreements(): Observable<ContractAgreement[]> {
    return this._contractAgreements.asObservable();
  }
  get contractTemplates(): Observable<ContractTemplate[]> {
    return this._contractTemplates.asObservable();
  }
  get contractClauses(): Observable<ContractClause[]> {
    return this._contractClauses.asObservable();
  }
  get uploadedTemplate(): Observable<any> {
    return this._uploadedTemplate.asObservable();
  }
  get uploadedClause(): Observable<any> {
    return this._uploadedClause.asObservable();
  }

  getContractCategories(): Subscription {
    // const contractUrl = '/api/contracts/categories';
    this._contractTypesByCategory = new BehaviorSubject<ContractType[]>([]);
    return this.http.get<any>(`${this.resourceUrl}/contracts/categories`).subscribe(
      data => {
        this.dataStore.contractCategories = data;
        this._contractCategories.next(Object.assign({}, this.dataStore).contractCategories);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract categories');
      }
    );
  }

  getAttributesMeta(): Observable<any> {
    return this.http.get<any>(`${this.resourceUrl}/contracts/attributesmeta`);
  }

  getContractTypesByCategory(categoryId: string | undefined): Subscription {
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    // const contractUrl = '/api/types/' + categoryId;
    if (typeof categoryId === 'undefined') {
      categoryId = '';
    }
    return this.http.get<any>(`${this.resourceUrl}/types/${categoryId}`).subscribe(
      data => {
        this.dataStore.contractTypesByCategory = data;
        this._contractTypesByCategory.next(Object.assign({}, this.dataStore).contractTypesByCategory);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract types');
      }
    );
  }
  getContractTypeVersions(contractTypeId: string): Subscription {
    // const contractTypeRevisionsUrl = '/api/contracts/types/revisions/'+contractTypeName;
    // const contractTypeRevisionsUrl = '/api/types/agreement/revisions/' + contractTypeId;
    return this.http.get<any>(`${this.resourceUrl}/types/agreement/revisions/${contractTypeId}`).subscribe(
      data => {
        this.dataStore.contractTypeRevisions = data;
        this._contractTypeRevisions.next(Object.assign({}, this.dataStore).contractTypeRevisions);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contractType Revisions');
      }
    );
  }
  getAgreementVersions(agreementId: string | undefined): Observable<any> {
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    // const contractAgreementRevisionsUrl = '/api/contracts/agreements/revisions/' + agreementName;
    if (typeof agreementId === 'undefined') {
      agreementId = '';
    }
    return this.http.get<any>(`${this.resourceUrl}/contracts/agreements/revisions/${agreementId}`);
  }
  rollBackAgreement(contractAgreement: ContractAgreement): Observable<any> {
    return this.http.put(`${this.resourceUrl}/contracts/agreements/rollback`, new Object(contractAgreement));
  }
  rollBackTemplate(contractTemplate: ContractTemplate): Observable<any> {
    return this.http.put(`${this.resourceUrl}/contracts/templates/rollback`, new Object(contractTemplate));
  }
  rollBackClause(contractClause: ContractClause): Observable<any> {
    return this.http.put(`${this.resourceUrl}/contracts/clauses/rollback`, new Object(contractClause));
  }
  getTemplateVersions(templateId: string | undefined): Observable<any> {
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    // const contractAgreementRevisionsUrl = '/api/contracts/agreements/revisions/' + agreementName;
    if (typeof templateId === 'undefined') {
      templateId = '';
    }
    return this.http.get<any>(`${this.resourceUrl}/contracts/templates/revisions/${templateId}`);
  }
  getClauseVersions(clauseId: string | undefined): Observable<any> {
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    // const contractAgreementRevisionsUrl = '/api/contracts/agreements/revisions/' + agreementName;
    if (typeof clauseId === 'undefined') {
      clauseId = '';
    }
    return this.http.get<any>(`${this.resourceUrl}/contracts/clauses/revisions/${clauseId}`);
  }
  getContractTypes(): Subscription {
    // const contractUrl = '/api/types/agreement';

    return this.http.get<any>(`${this.resourceUrl}/types/agreement`).subscribe(
      data => {
        this.dataStore.contractTypes = data;
        this._contractTypes.next(Object.assign({}, this.dataStore).contractTypes);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract types');
      }
    );
  }

  getContractType(id: string): Observable<ContractType> {
    // const contractUrl = '/api/types/agreement/' + id;

    return this.http.get<ContractType>(`${this.resourceUrl}/types/agreement/${id}`);
  }

  getContractAgreements(): Subscription {
    // const contractUrl = '/api/contracts/agreements';

    return this.http.get<any>(`${this.resourceUrl}/contracts/agreements`).subscribe(
      data => {
        this.dataStore.contractAgreements = data;
        this._contractAgreements.next(Object.assign({}, this.dataStore).contractAgreements);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract agreements');
      }
    );
  }

  getContractAgreementsByStatus(entityStatus?: EntityStatus): Observable<ContractAgreement[]> {
    const status = entityStatus ? '/q?status=' + entityStatus : '';
    return this.http.get<ContractAgreement[]>(`${this.resourceUrl}/contracts/agreements${status}`);
  }

  getContractAgreement(id: string): Observable<ContractAgreement> {
    // const contractUrl = '/api/contracts/agreements/' + id;
    return this.http.get<ContractAgreement>(`${this.resourceUrl}/contracts/agreements/${id}`);
  }

  getApprovedRequests(): Observable<ApprovedRequest[]> {
    // const approvedRequestUrl = 'http://localhost:3000/requests';
    return this.http.get<ApprovedRequest[]>(`${this.resourceUrl}/requests`);
  }
  addContractCategory(contractCategoryForm: any): Observable<any> {
    // const addContractCategoryUrl = '/api/contracts/categories';
    return this.http.post(`${this.resourceUrl}/contracts/categories`, new Object(contractCategoryForm));
  }

  addContractType(contractTypeForm: any): Observable<any> {
    // const addContractTypeUrl = '/api/types/agreement';
    return this.http.post(`${this.resourceUrl}/types/agreement`, new Object(contractTypeForm));
  }

  updateContractType(contractTypeForm: any): Observable<any> {
    // const addContractTypeUrl = '/api/types/agreement';
    return this.http.put(`${this.resourceUrl}/types/agreement`, new Object(contractTypeForm));
  }

  // addContractAgreement(contractAgreementForm: any): Observable<any> {
  //   // const addContractAgreementUrl = '/api/contracts/agreements';
  //   return this.http.post(`${this.resourceUrl}/contracts/agreements`, new Object(contractAgreementForm));
  // }
  createAgreementFiles(contractAgreementForm: any): Observable<any> {
    // const createAgreementFilesUrl = '/api/contracts/agreements/create';
    return this.http.post(`${this.resourceUrl}/contracts/agreements/create`, new Object(contractAgreementForm));
  }
  updateContractAgreement(contractAgreementForm: any): Observable<any> {
    // const addContractAgreementUrl = '/api/contracts/agreements';
    return this.http.put(`${this.resourceUrl}/contracts/agreements`, new Object(contractAgreementForm));
  }

  addContractTemplate(contractTemplateForm: any): Observable<any> {
    // const addContractTemplateUrl = '/api/contracts/templates';
    return this.http.post(`${this.resourceUrl}/contracts/templates`, new Object(contractTemplateForm));
  }
  getContractTemplates(): Subscription {
    // const getTemplatesUrl = '/api/contracts/templates';

    return this.http.get<any>(`${this.resourceUrl}/contracts/templates`).subscribe(
      data => {
        this.dataStore.contractTemplates = data;
        this._contractTemplates.next(Object.assign({}, this.dataStore).contractTemplates);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract templates');
      }
    );
  }
  getContractTemplate(id: string): Observable<ContractTemplate> {
    // const contractUrl = '/api/contracts/templates/' + id;
    return this.http.get<ContractTemplate>(`${this.resourceUrl}/contracts/templates/${id}`);
  }
  publishTemplate(contractTemplate: ContractTemplate): Observable<any> {
    // const updateTemplateUrl = '/api/contracts/templates';
    return this.http.put(`${this.resourceUrl}/contracts/templates/publish`, new Object({ id: contractTemplate.id }));
  }
  updateContractTemplate(contractTemplateForm: any): Observable<any> {
    // const updateTemplateUrl = '/api/contracts/templates';
    return this.http.put(`${this.resourceUrl}/contracts/templates`, new Object(contractTemplateForm));
  }
  updateTemplateStatus(templateId: number, status: any): Observable<any> {
    // const updateTemplateStatusUrl = '/api/contracts/templates/'+templateId;
    return this.http.put(`${this.resourceUrl}/contracts/templates/${templateId}`, status);
  }
  getContractClause(id: string): Observable<ContractClause> {
    // const contractUrl = '/api/contracts/templates/' + id;
    return this.http.get<ContractClause>(`${this.resourceUrl}/contracts/clauses/${id}`);
  }
  getContractClauses(): Subscription {
    // const getTemplatesUrl = '/api/contracts/templates';

    return this.http.get<any>(`${this.resourceUrl}/contracts/clauses`).subscribe(
      data => {
        this.dataStore.contractClauses = data;
        this._contractClauses.next(Object.assign({}, this.dataStore).contractClauses);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract clauses');
      }
    );
  }
  publishClause(contractClause: ContractClause): Observable<any> {
    // const updateTemplateUrl = '/api/contracts/templates';
    return this.http.put(`${this.resourceUrl}/contracts/clauses/publish`, new Object({ id: contractClause.id }));
  }
  addContractClause(contractClauseForm: any): Observable<any> {
    // const addContractTemplateUrl = '/api/contracts/clauses';
    return this.http.post(`${this.resourceUrl}/contracts/clauses`, new Object(contractClauseForm));
  }
  updateContractClause(contractClauseForm: any): Observable<any> {
    // const updateTemplateUrl = '/api/contracts/templates';
    return this.http.put(`${this.resourceUrl}/contracts/clauses`, new Object(contractClauseForm));
  }
  updateClauseStatus(clauseId: number, status: any): Observable<any> {
    // const updateTemplateStatusUrl = '/api/contracts/clauses/'+templateId;
    return this.http.put(`${this.resourceUrl}/contracts/clauses/${clauseId}`, status);
  }
  publishAgreement(contractAgreement: ContractAgreement): Observable<any> {
    // const publishAgreementUrl = '/api/contracts/agreements/publish';
    return this.http.put(`${this.resourceUrl}/contracts/agreements/publish`, new Object({ id: contractAgreement.id }));
  }
  approveAgreement(contractAgreement: ContractAgreement): Observable<any> {
    // const publishAgreementUrl = '/api/contracts/agreements/publish';
    return this.http.put(`${this.resourceUrl}/contracts/agreements/approve`, new Object({ id: contractAgreement.id }));
  }
  approveTemplate(contractTemplate: ContractTemplate): Observable<any> {
    // const publishAgreementUrl = '/api/contracts/agreements/publish';
    return this.http.put(`${this.resourceUrl}/contracts/templates/approve`, new Object({ id: contractTemplate.id }));
  }
  approveClause(contractClause: ContractClause): Observable<any> {
    // const publishAgreementUrl = '/api/contracts/agreements/publish';
    return this.http.put(`${this.resourceUrl}/contracts/clauses/approve`, new Object({ id: contractClause.id }));
  }
  sendAgreementForSignature(sendForSignatureForm: any): Observable<any> {
    return this.http.post(`${this.resourceUrl}/contracts/agreements/signature`, new Object(sendForSignatureForm));
  }
  downloadPDF(url: string): any {
    // const options = { responseType: ResponseContentType.Blob  };
    // const options = { responseType: 'blob' as 'json'  };
    //
    // return this.http.get(url, options).pipe(map(
    //   (res) => new Blob([res], { type: 'application/pdf' })));
    return null;
  }
  get apiServices(): Observable<ApiService[]> {
    return this._apiServices.asObservable();
  }
  getApiServices(): Subscription {
    // const contractUrl = this.contractManagerBase_Url+'/oauthapiservices';

    return this.http.get<any>(`${this.resourceUrl}/contracts/oauthapiservices`).subscribe(
      data => {
        this.dataStore.apiServices = data;
        this._apiServices.next(Object.assign({}, this.dataStore).apiServices);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch contract agreements');
      }
    );
  }
  addApiService(apiServiceForm: any): Observable<any> {
    // const addApiServiceUrl = this.contractManagerBase_Url+'/oauthapiservices';
    return this.http.post(`${this.resourceUrl}/contracts/oauthapiservices`, new Object(apiServiceForm));
  }
  updateAuthorizationStatus(apiServiceForm: any): Observable<any> {
    // const updateAuthorizationUrl = this.contractManagerBase_Url+'/oauthapiservices/authorized';
    return this.http.put(`${this.resourceUrl}/contracts/oauthapiservices/authorized`, new Object(apiServiceForm));
  }
  getUploadedTemplate(): Subscription {
    return this.http.get<string>(`${this.resourceUrl}/contracts/templates/upload`).subscribe(
      data => {
        this.dataStore.uploadedTemplate = data;
        this._uploadedTemplate.next(Object.assign({}, this.dataStore).uploadedTemplate);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch uploaded template id');
      }
    );
  }
  getUploadedClause(): Subscription {
    return this.http.get<string>(`${this.resourceUrl}/contracts/clauses/upload`).subscribe(
      data => {
        this.dataStore.uploadedClause = data;
        this._uploadedClause.next(Object.assign({}, this.dataStore).uploadedClause);
      },
      error => {
        // eslint-disable-next-line no-console
        console.log('Failed to fetch uploaded clause id');
      }
    );
  }

  deleteAgreement(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.resourceUrl}/contracts/agreements/d/${id}`);
  }

  deleteTemplate(id: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this.resourceUrl}/contracts/template/d/${id}`);
  }

  deleteClause(id: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.resourceUrl}/contracts/clause/d/${id}`);
  }

  rejectAgreement(contractAgreement: ContractAgreement): Observable<HttpResponse<any>> {
    return this.http.put<HttpResponse<HttpStatusCode>>(
      `${this.resourceUrl}/contracts/agreements/reject`,
      new Object({ id: contractAgreement.id })
    );
  }
}
