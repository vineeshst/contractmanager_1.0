import { Pipe, PipeTransform } from '@angular/core';
import { UserGroup } from '../../contractmanager/models/user-group';

@Pipe({ name: 'userGroupFilter' })
export class UserGroupFilterPipe implements PipeTransform {
  /**
   * Transform
   *
   * @param {any[]} items
   * @param {string} searchText
   * @returns {any[]}
   */
  transform(items: UserGroup[] | undefined, searchText: string): any {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (!items) {
      return [];
    }
    if (!searchText) {
      return items;
    }
    searchText = searchText.toLocaleLowerCase();

    return items.filter(it => it.name?.toLocaleLowerCase().includes(searchText));
  }
}
