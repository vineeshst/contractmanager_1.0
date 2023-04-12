import { Pipe, PipeTransform } from '@angular/core';
import { IUser } from '../../admin/user-management/user-management.model';

@Pipe({ name: 'userFilter' })
export class UserFilterPipe implements PipeTransform {
  /**
   * Transform
   *
   * @param {any[]} items
   * @param {string} searchText
   * @returns {any[]}
   */
  transform(items: IUser[] | undefined, searchText: string): any {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (!items) {
      return [];
    }
    if (!searchText) {
      return items;
    }
    searchText = searchText.toLocaleLowerCase();

    return items.filter(
      it => it.firstName?.toLocaleLowerCase().includes(searchText) ?? it.lastName?.toLocaleLowerCase().includes(searchText)
    );
  }
}
