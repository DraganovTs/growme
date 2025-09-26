import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, filter, from, Observable, switchMap, throwError, take } from "rxjs"; // ADD 'take' HERE
import { environment } from "src/app/environment/environments";
import { KeycloakService } from "src/app/services/keycloak.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    private authChecked = false;

    constructor(private keycloakService: KeycloakService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
           console.log('🎯 INTERCEPTOR IS WORKING! URL:', req.url);
    
    // Test: Add a custom header to see if interceptor is modifying requests
    const testReq = req.clone({
        setHeaders: { 'X-Interceptor-Test': 'working' }
    });
    
    return next.handle(testReq);
    }

    private addTokenToRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return from(this.keycloakService.getToken()).pipe(
            switchMap(token => {
                if (token) {
                    console.log('✅ Token attached to request:', req.url);
                    console.log('📎 Token preview:', token.substring(0, 50) + '...');
                    const authReq = req.clone({
                        setHeaders: { 
                            Authorization: `Bearer ${token}` 
                        }
                    });
                    return next.handle(authReq);
                }
                console.warn('❌ No token available for request:', req.url);
                return this.handleUnauthenticated(req, next);
            }),
            catchError(error => {
                if (error.status === 401 && !this.authChecked) {
                    this.authChecked = true;
                    console.log('🔄 Retrying request with fresh token:', req.url);
                    return this.addTokenToRequest(req, next);
                }
                return throwError(() => error);
            })
        );
    }

    private handleUnauthenticated(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        console.warn('⚠️ Proceeding without authentication for:', req.url);
        return next.handle(req);
    }

    private handleError(error: any): Observable<HttpEvent<any>> {
        console.error('💥 Interceptor error:', error);
        if (error.status === 401 || error.status === 403) {
            console.log('🔒 Authentication error, logging out...');
            this.keycloakService.logout();
        }
        return throwError(() => error);
    }

 private shouldSkipAuth(url: string): boolean {
    const skipUrls = [
        'keycloak', 
        'assets', 
        '.json',
        environment.keycloakUrl,
        '/growme/categories',
        '/growme/owners', 
        '/growme/products',
        '/growme/bids',
        '/growme/tasks',
        '/growme/basket',
        '/growme/deliverymethods',
        '/actuator'
    ];
    
    const shouldSkip = skipUrls.some(skipUrl => url.includes(skipUrl));
    console.log(`🛡️ Auth check for ${url}: ${shouldSkip ? 'SKIP' : 'REQUIRE AUTH'}`);
    return shouldSkip;
}
}

