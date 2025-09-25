import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, filter, from, Observable, switchMap, throwError } from "rxjs";
import { environment } from "src/app/environment/environments";
import { KeycloakService } from "src/app/services/keycloak.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
private authChecked = false;

    constructor(private keycloakService: KeycloakService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
       
        if (this.shouldSkipAuth(req.url)) {
            return next.handle(req);
        }

        console.log('Interceptor processing request to:', req.url);

        return this.keycloakService.initialized$.pipe(
            filter(initialized => initialized),
            take(1),
            switchMap(() => this.keycloakService.isAuthenticated$),
            take(1),
            switchMap(authenticated => {
                if (!authenticated) {
                    console.warn('User not authenticated for request:', req.url);
                    return this.handleUnauthenticated(req, next);
                }
                return this.addTokenToRequest(req, next);
            }),
            catchError(error => this.handleError(error))
        );
    }

    private addTokenToRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return from(this.keycloakService.getToken()).pipe(
            switchMap(token => {
                if (token) {
                    console.log('Token attached to request:', req.url);
                    const authReq = req.clone({
                        setHeaders: { 
                            Authorization: `Bearer ${token}` 
                        }
                    });
                    return next.handle(authReq);
                }
                console.warn('No token available for request:', req.url);
                return this.handleUnauthenticated(req, next);
            }),
            // Auto-retry on 401 errors (optional)
            catchError(error => {
                if (error.status === 401 && !this.authChecked) {
                    this.authChecked = true;
                    console.log('Retrying request with fresh token:', req.url);
                    return this.addTokenToRequest(req, next);
                }
                return throwError(() => error);
            })
        );
    }

    private handleUnauthenticated(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // You can choose to block the request or let it proceed
        console.warn('Proceeding without authentication for:', req.url);
        return next.handle(req);
    }

    private handleError(error: any): Observable<HttpEvent<any>> {
        console.error('Interceptor error:', error);
        if (error.status === 401 || error.status === 403) {
            this.keycloakService.logout();
        }
        return throwError(() => error);
    }

    private shouldSkipAuth(url: string): boolean {
        const skipUrls = [
            'keycloak', 
            'assets', 
            '.json',
            environment.keycloakUrl // Skip Keycloak server calls
        ];
        return skipUrls.some(skipUrl => url.includes(skipUrl));
    }
}

function take(arg0: number): import("rxjs").OperatorFunction<boolean, unknown> {
    throw new Error("Function not implemented.");
}
