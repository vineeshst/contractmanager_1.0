import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ContractTypeStatus } from '../shared/constants/contract-type.constants';
import { SERVER_API_URL } from '../../app.constants';

@Injectable({
  providedIn: 'root',
})
export class ContractTypeService {
  public resourceUrl = SERVER_API_URL + 'api/types';

  constructor(private http: HttpClient) {}

  updateContractTypeStatus(contractTypeId: number, status: any): Observable<any> {
    return this.http.put(`${this.resourceUrl}/agreement/${contractTypeId}`, status);
  }

  getContractTypeVersions(contractTypeId: string): Observable<any> {
    const contractTypeRevisionsUrl = '/api/types/agreement/revisions/' + contractTypeId;
    return this.http.get<any>(contractTypeRevisionsUrl);
  }
}
