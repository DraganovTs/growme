import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { map, take } from 'rxjs/operators';
import { KeycloakService } from 'src/app/services/keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    private readonly LOGIN_ROUTE = '/login';

  constructor(
    private keycloakService: KeycloakService,
    private router: Router
  ) {}

   canActivate(): Observable<boolean> {
    return this.keycloakService.isAuthenticated$.pipe(
      take(1),
      map(isAuthenticated => {
        if (!isAuthenticated) {
          this.router.navigate([this.LOGIN_ROUTE]);
          return false;
        }
        return true;
      })
    );
  }
}