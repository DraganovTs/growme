import { Injectable, Inject, PLATFORM_ID, NgZone } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, from, of, throwError } from 'rxjs';
import { catchError, map, switchMap, tap, filter, take } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../environment/environments';
import Keycloak from 'keycloak-js';
import { CartService } from './cart-service';
import { UserProfile } from '../shared/model/UserProfile';

@Injectable({ providedIn: 'root' })
export class KeycloakService {
 private keycloak: Keycloak | null = null;
    private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    private userProfileSubject = new BehaviorSubject<UserProfile | null>(null);
    private initializedSubject = new BehaviorSubject<boolean>(false); 

    public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
    public userProfile$ = this.userProfileSubject.asObservable();
    public initialized$ = this.initializedSubject.asObservable();
    private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    private ngZone: NgZone,
    private cartService: CartService,
    @Inject(PLATFORM_ID) private platformId: object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    this.initializeFromStorage();
  }

    private initializeFromStorage(): void {
    if (!this.isBrowser) return;
    
    this.isAuthenticatedSubject.next(JSON.parse(localStorage.getItem('isAuthenticated') || 'false'));
    const savedProfile = localStorage.getItem('userProfile');
    if (savedProfile) {
      this.userProfileSubject.next(JSON.parse(savedProfile));
    }
  }

 async init(): Promise<void> {
    console.log('🔧 DOCKER DEBUG - Keycloak initialization started...');
    console.log('🔧 DOCKER DEBUG - Environment:', {
        keycloakUrl: environment.keycloakUrl,
        keycloakRealm: environment.keycloakRealm,
        keycloakClientId: environment.keycloakClientId
    });
        if (!this.isBrowser) {
            this.initializedSubject.next(true);
            return;
        }

        this.keycloak = new Keycloak({
            url: environment.keycloakUrl,
            realm: environment.keycloakRealm,
            clientId: environment.keycloakClientId,
        });

        this.keycloak.onAuthSuccess = () => {
        console.log('🔧 DOCKER DEBUG - Keycloak auth success');
    };
    
    this.keycloak.onAuthError = (error) => {
        console.error('🔧 DOCKER DEBUG - Keycloak auth error:', error);
    };
    
    this.keycloak.onAuthRefreshSuccess = () => {
        console.log('🔧 DOCKER DEBUG - Keycloak auth refresh success');
    };
    
    this.keycloak.onAuthRefreshError = () => {
        console.error('🔧 DOCKER DEBUG - Keycloak auth refresh error');
    };
    
    this.keycloak.onAuthLogout = () => {
        console.log('🔧 DOCKER DEBUG - Keycloak auth logout');
    };
    
    this.keycloak.onTokenExpired = () => {
        console.log('🔧 DOCKER DEBUG - Keycloak token expired');
    };

        try {
                  console.log('🔧 DOCKER DEBUG - Starting Keycloak init with login-required...');

            const authenticated = await this.keycloak.init({
                onLoad: 'login-required',
                checkLoginIframe: false,
            });

            console.log('🔧 DOCKER DEBUG - Keycloak init completed, authenticated:', authenticated);
        console.log('🔧 DOCKER DEBUG - Keycloak token:', this.keycloak.token ? 'Present' : 'Missing');
        console.log('🔧 DOCKER DEBUG - Keycloak tokenParsed:', this.keycloak.tokenParsed);

            this.setAuthenticationState(authenticated);
            this.initializedSubject.next(true); 

            if (authenticated && this.keycloak.tokenParsed) {
                const userRoles = this.keycloak.tokenParsed.realm_access?.roles || [];
                const userId = this.keycloak.tokenParsed.sub || '';
                localStorage.setItem('userId', userId);

                console.log('🔧 DOCKER DEBUG - User roles:', userRoles);
            console.log('🔧 DOCKER DEBUG - User ID:', userId);

                this.syncUserWithBackend().pipe(
                    switchMap(() => this.checkProfileCompletion()),
                    take(1)
                ).subscribe((profileComplete) => {
                                  console.log('🔧 DOCKER DEBUG - Profile complete check result:', profileComplete);

                    this.ngZone.run(() => {
                        if (profileComplete && this.hasRequiredRole(userRoles)) {
                             console.log('🔧 DOCKER DEBUG - Redirecting to home');
                            this.router.navigate(['/']); 
                        } else {
                            console.log('🔧 DOCKER DEBUG - Redirecting to complete-profile');
                            this.router.navigate(['/complete-profile']);
                        }
                    });
                });

                this.startTokenRefresh();
            } else {
            console.log('🔧 DOCKER DEBUG - User not authenticated after init');
        }
        } catch (error) {
           console.error('🔧 DOCKER DEBUG - Keycloak initialization failed:', error);
            this.setAuthenticationState(false);
            this.initializedSubject.next(true); 
        }
    }

  login(): Observable<void> {
    return from(this.keycloak?.login() || Promise.resolve());
  }

  register(): Observable<void> {
        console.log('🔧 DOCKER DEBUG - Starting registration flow...');

    if (!this.keycloak) return of(undefined);
        console.error('🔧 DOCKER DEBUG - Keycloak not available for registration');

    return from(this.keycloak.register()).pipe(
      switchMap(() => {
            console.log('🔧 DOCKER DEBUG - Initializing after registration with check-sso...');
            return from(this.keycloak!.init({ onLoad: 'check-sso', checkLoginIframe: false }));
        }),
      switchMap((authenticated) => {
                                console.log('🔧 DOCKER DEBUG - Post-registration authenticated:', authenticated);
            console.log('🔧 DOCKER DEBUG - Post-registration token:', this.keycloak?.token ? 'Present' : 'Missing');
            console.log('🔧 DOCKER DEBUG - Post-registration tokenParsed:', this.keycloak?.tokenParsed);

        if (!authenticated || !this.keycloak?.tokenParsed) {
                          console.error('🔧 DOCKER DEBUG - Authentication failed after registration');
                console.log('🔧 DOCKER DEBUG - Trying manual login after registration...');

          throw new Error('Authentication failed');
        }

        const userRoles = this.keycloak.tokenParsed.realm_access?.roles || [];
        this.setAuthenticationState(true);

        return this.syncUserWithBackend().pipe(
          switchMap(() => this.checkProfileCompletion()),
          tap((profileComplete) => {
            const redirect = profileComplete && this.hasRequiredRole(userRoles)
              ? '/dashboard'
              : '/update';
            this.ngZone.run(() => this.router.navigate([redirect]));
          }),
          map(() => void 0)
        );
      }),
      catchError((error) => {
        console.error('Registration failed:', error);
        this.setAuthenticationState(false);
        return of(void 0);
      })
    );
  }

  async logout(): Promise<void> {
    if (this.keycloak) {
      const cart = this.cartService.getCurrentCart?.() ?? null;
      if (cart) this.cartService.deleteCart(cart);
      await this.keycloak.logout();
      this.setAuthenticationState(false);
      this.router.navigate(['/login']);
    }
  }

  updateRoles(userId: string, roles: string[]): Observable<void> {
    return this.http.post<void>(`${environment.userApi}/update-roles/${userId}`, { roles }).pipe(
      tap(() => alert('Roles updated successfully!')),
      catchError((error) => {
        console.error('Failed to update roles:', error);
        return of();
      })
    );
  }

async getToken(): Promise<string | null> {
    if (!this.keycloak) {
        console.warn('Keycloak instance not available');
        return null;
    }
    
    if (!this.keycloak.authenticated) {
        console.warn('Keycloak not authenticated');
        return null;
    }
    
    try {
        console.log('Before token refresh - token exists:', !!this.keycloak.token);
        const refreshed = await this.keycloak.updateToken(30);
        console.log('Token refreshed:', refreshed);
        console.log('After token refresh - token exists:', !!this.keycloak.token);
        
        if (this.keycloak.token) {
            console.log('Token length:', this.keycloak.token.length);
          
            this.decodeToken(this.keycloak.token);
        }
        
        return this.keycloak.token ?? null;
    } catch (error) {
        console.error('Token refresh failed:', error);
        await this.logout();
        return null;
    }
}

decodeToken(token: string): void {
  
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        const tokenData = JSON.parse(decoded);
        console.log('audience:', tokenData.aud);
        console.log('Token expiration:', new Date(tokenData.exp * 1000));
        console.log('Token issued at:', new Date(tokenData.iat * 1000));
        console.log('Token claims:', Object.keys(tokenData));
    } catch (error) {
        console.error('Failed to decode token:', error);
    }
}

  private setAuthenticationState(authenticated: boolean): void {
    this.isAuthenticatedSubject.next(authenticated);
    localStorage.setItem('isAuthenticated', JSON.stringify(authenticated));
  }

  private startTokenRefresh(): void {
    setInterval(async () => {
      if (!this.keycloak) return;
      try {
        await this.keycloak.updateToken(60);
      } catch (error) {
        console.error('Token refresh failed', error);
        this.logout();
      }
    }, 60000);
  }

  getUsername(): string | null {
    if (!this.keycloak?.tokenParsed) {
        console.warn('No token parsed available');
        return null;
    }
    const username = this.keycloak.tokenParsed['preferred_username'];
    console.log('Retrieved username:', username);
    return username || null;
}

  getUserId(): string | null {
    if (!this.keycloak?.tokenParsed) {
        console.warn('No token parsed available');
        return localStorage.getItem('userId');
    }
    const userId = this.keycloak.tokenParsed.sub;
    console.log('Retrieved userId:', userId);
    return userId || localStorage.getItem('userId');
}

  getEmail(): string {
    if (!this.keycloak?.tokenParsed) {
        console.warn('No token parsed available');
        return '';
    }
    const email = this.keycloak.tokenParsed['email'];
    console.log('Retrieved email:', email);
    return email || '';
}

  hasRole(role: string): boolean {
    return this.keycloak?.tokenParsed?.realm_access?.roles?.includes(role) ?? false;
  }

  private hasRequiredRole(userRoles: string[]): boolean {
    const requiredRoles = ['SELLER', 'BUYER', 'ADMIN'];
    return userRoles.some(role => requiredRoles.includes(role.toUpperCase()));
  }

private syncUserWithBackend(): Observable<void> {
    if (!this.keycloak?.tokenParsed) {
        console.warn("No Keycloak token parsed, skipping sync.");
        return of(undefined);
    }

    const userData = {
        userId: this.keycloak.tokenParsed.sub,
        username: this.keycloak.tokenParsed['preferred_username'],
        email: this.keycloak.tokenParsed['email'],
    };

    console.log('Sync payload:', userData);

    // Let the interceptor handle the Authorization header
    return this.http.post(`${environment.userApi}/sync`, userData, {
        observe: 'response'
    }).pipe(
        tap(response => {
            console.log('Sync response status:', response.status);
            if (response.status === 202 || response.status === 200 || response.status === 201) {
                console.log('User synchronized successfully');
            }
        }),
        map(() => undefined),
        catchError(error => {
            console.error('User synchronization failed:', error);
            return of(undefined);
        })
    );
}

  private checkProfileCompletion(): Observable<boolean> {
    const userId = this.getUserId();
    if (!userId) {
      return of(false);
    }

    return this.http.get<boolean>(`${environment.userApi}/profile-complete/${userId}`).pipe(
      catchError(err => {
        console.error('Profile completion check failed', err);
        return of(false);
      })
    );
  }


}
