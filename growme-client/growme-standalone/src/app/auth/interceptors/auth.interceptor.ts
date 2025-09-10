import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, from, Observable, switchMap, throwError } from "rxjs";
import { KeycloakService } from "src/app/services/keycloak.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private keycloakService: KeycloakService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        console.log('Interceptor called for URL:', req.url);
        
        return from(this.keycloakService.getToken()).pipe(
            switchMap(token => {
                if (token) {
                    console.log('Token found, attaching to request');
                    console.log('Token length:', token.length);
                    const authReq = req.clone({
                        setHeaders: {
                            Authorization: `Bearer ${token}`
                        }
                    });
                    return next.handle(authReq);
                } else {
                    console.warn('No token available for request:', req.url);
                    return next.handle(req);
                }
            }),
            catchError(error => {
                console.error('Interceptor error:', error);
                if (error.status === 401 || error.status === 403) {
                    this.keycloakService.logout();
                }
                return throwError(() => error);
            })
        );
    }
}