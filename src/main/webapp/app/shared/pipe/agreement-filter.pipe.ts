import { Pipe, PipeTransform } from '@angular/core';
import { ContractAgreement } from '../../contractmanager/models/contractAgreement';
import { UserGroup } from '../../contractmanager/models/user-group';

@Pipe({ name: 'agreementFilter' })
export class AgreementFilterPipe implements PipeTransform {
  /**
   * Transform
   *
   * @param {any[]} items
   * @param {string} searchText
   * @returns {any[]}
   */
  transform(items: ContractAgreement[] | undefined, searchText: string): any {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (!items) {
      return [];
    }
    if (!searchText) {
      return items;
    }
    searchText = searchText.toLowerCase();

    return items.filter(it => it.entityName.toLocaleLowerCase().includes(searchText));
  }
}
