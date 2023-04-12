import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Privilege } from '../../contractmanager/models/security-group';
import { AccountService } from './account.service';
import { StateStorageService } from './state-storage.service';

@Injectable({
  providedIn: 'root',
})
export class RouteSecurityService implements CanActivate {
  constructor(private router: Router, private accountService: AccountService, private stateStorageService: StateStorageService) {}
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.accountService.identity().pipe(
      map(account => {
        if (account) {
          const privilege = route.data['privilege'] as Privilege;

          if (this.accountService.hasPrivilege(privilege)) {
            return true;
          }
          this.router.navigate(['accessdenied']);
          return false;
        }

        this.stateStorageService.storeUrl(state.url);
        this.router.navigate(['/login']);
        return false;
      })
    );
  }
}
