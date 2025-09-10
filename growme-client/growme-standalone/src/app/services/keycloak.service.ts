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

  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public userProfile$ = this.userProfileSubject.asObservable();
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
    if (!this.isBrowser) return;

    this.keycloak = new Keycloak({
      url: environment.keycloakUrl,
      realm: environment.keycloakRealm,
      clientId: environment.keycloakClientId,
    });

    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: false,
      });

      this.setAuthenticationState(authenticated);

      if (authenticated && this.keycloak.tokenParsed) {
        const userRoles = this.keycloak.tokenParsed.realm_access?.roles || [];
        const userId = this.keycloak.tokenParsed.sub || '';
        localStorage.setItem('userId', userId);

        this.syncUserWithBackend().pipe(
          switchMap(() => this.checkProfileCompletion()),
          take(1)
        ).subscribe((profileComplete) => {
          this.ngZone.run(() => {
            if (profileComplete && this.hasRequiredRole(userRoles)) {
              this.router.navigate(['/']); 
            } else {
              this.router.navigate(['/complete-profile']);
            }
          });
        });

        this.startTokenRefresh();
      }
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      this.setAuthenticationState(false);
    }
  }

  login(): Observable<void> {
    return from(this.keycloak?.login() || Promise.resolve());
  }

  register(): Observable<void> {
    if (!this.keycloak) return of(undefined);

    return from(this.keycloak.register()).pipe(
      switchMap(() =>
        from(this.keycloak!.init({ onLoad: 'check-sso', checkLoginIframe: false }))
      ),
      switchMap((authenticated) => {
        if (!authenticated || !this.keycloak?.tokenParsed) {
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
    // Check if Keycloak instance exists and is authenticated
    if (!this.keycloak || !this.keycloak.authenticated) {
        return null;
    }
    
    try {
        // Refresh token if needed (within 30 seconds of expiration)
        await this.keycloak.updateToken(30); 
        
        // Return token or null if undefined
        return this.keycloak.token ?? null;
    } catch (error) {
        console.error('Token refresh failed:', error);
        await this.logout();
        return null;
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
    return this.keycloak?.tokenParsed?.['preferred_username'] || null;
  }

  getUserId(): string | null {
    return this.keycloak?.tokenParsed?.sub || localStorage.getItem('userId');
  }

  getEmail(): string {
    return this.keycloak?.tokenParsed?.['email'] ?? '';
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
