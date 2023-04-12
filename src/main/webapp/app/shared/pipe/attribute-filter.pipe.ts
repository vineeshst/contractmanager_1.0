import { Pipe, PipeTransform } from '@angular/core';
import { AttributeConfig } from '../../contractmanager/models/attributeConfig';

@Pipe({ name: 'attributeFilter' })
export class AttributeFilterPipe implements PipeTransform {
  /**
   * Transform
   *
   * @param {any[]} items
   * @param {string} searchText
   * @returns {any[]}
   */
  transform(items: AttributeConfig[], searchText: string): any[] {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (!items) {
      return [];
    }
    if (!searchText) {
      return items;
    }
    searchText = searchText.toLocaleLowerCase();

    return items.filter(it => it.attributeName.toLocaleLowerCase().includes(searchText));
  }
}
