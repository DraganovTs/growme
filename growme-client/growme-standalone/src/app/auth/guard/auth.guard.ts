import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { from, Observable, throwError } from 'rxjs';

import { catchError, map, switchMap, take } from 'rxjs/operators';
import { KeycloakService } from 'src/app/services/keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements HttpInterceptor {

private readonly EXCLUDED_URLS = ['keycloak', 'external-api'];

    constructor(private keycloakService: KeycloakService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.shouldSkipInterceptor(req.url)) {
            return next.handle(req);
        }

        return from(this.keycloakService.getToken()).pipe(
            switchMap(token => {
                if (token) {
                    const authenticatedReq = this.addAuthHeader(req, token);
                    return next.handle(authenticatedReq);
                }
                return next.handle(req);
            }),
            catchError(error => {
                // Handle token refresh errors or other issues
                if (error.status === 401 || error.status === 403) {
                    this.keycloakService.logout();
                }
                return throwError(() => error);
            })
        );
    }

    private shouldSkipInterceptor(url: string): boolean {
        return this.EXCLUDED_URLS.some(excluded => url.includes(excluded));
    }

    private addAuthHeader(req: HttpRequest<any>, token: string): HttpRequest<any> {
        return req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}