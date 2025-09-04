import { Injectable, Inject, PLATFORM_ID, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
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

  private readonly REQUIRED_ROLES = ['SELLER', 'BUYER', 'ADMIN'];
  private readonly TOKEN_REFRESH_INTERVAL = 60000;
  private readonly TOKEN_EXPIRY_BUFFER = 30;

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

    const isAuthenticated = JSON.parse(localStorage.getItem('isAuthenticated') || 'false');
    this.isAuthenticatedSubject.next(isAuthenticated);

    const savedProfile = localStorage.getItem('userProfile');
    if (savedProfile) {
      this.userProfileSubject.next(JSON.parse(savedProfile));
    }
  }

  async init(): Promise<void> {
    if (!this.isBrowser) return;

    try {
      this.keycloak = new Keycloak({
        url: environment.keycloakUrl,
        realm: environment.keycloakRealm,
        clientId: environment.keycloakClientId,
      });

      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: false,
      });

      this.setAuthenticationState(authenticated);

      if (authenticated && this.keycloak.tokenParsed) {
        await this.handleSuccessfulAuthentication();
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
          console.log('Profile complete:', profileComplete);
          const redirect = profileComplete && this.hasRequiredRole(userRoles)
            ? '/dashboard'
            : '/update';
          this.ngZone.run(() => this.router.navigate([redirect]));
        }),
        catchError((error) => {
          console.error('Sync failed, but continuing:', error);
          this.ngZone.run(() => this.router.navigate(['/update']));
          return of(void 0);
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

    private async handleSuccessfulAuthentication(): Promise<void> {
    if (!this.keycloak?.tokenParsed) return;

    const userRoles = this.keycloak.tokenParsed.realm_access?.roles || [];
    const userId = this.keycloak.tokenParsed.sub || '';

    localStorage.setItem('userId', userId);

    try {
      await this.syncUserWithBackend().toPromise();
      const profileComplete = await this.checkProfileCompletion().toPromise();

      this.ngZone.run(() => {
        this.navigateBasedOnAuthStatus(profileComplete, userRoles);
      });

      this.startTokenRefresh();
    } catch (error) {
      console.error('Authentication handling failed:', error);
    }
  }

    private navigateBasedOnAuthStatus(profileComplete: boolean | undefined, userRoles: string[]): void {
    if (profileComplete && this.hasRequiredRole(userRoles)) {
      this.router.navigate(['/']);
    } else {
      this.router.navigate(['/complete-profile']);
    }
  }

    async getToken(): Promise<string | null> {
    if (!this.keycloak?.authenticated) {
      return null;
    }

    try {
      await this.keycloak.updateToken(this.TOKEN_EXPIRY_BUFFER);
      return this.keycloak.token ?? null;
    } catch (error) {
      console.error('Token refresh failed:', error);
      await this.logout();
      return null;
    }
  }

  async logout(): Promise<void> {
    if (!this.keycloak) return;

    try {
      const cart = this.cartService.getCurrentCart?.() ?? null;
      if (cart) {
        this.cartService.deleteCart(cart);
      }

      await this.keycloak.logout();
      this.clearAuthenticationState();
      this.router.navigate(['/login']);
    } catch (error) {
      console.error('Logout failed:', error);
      this.clearAuthenticationState();
    }
  }

    private clearAuthenticationState(): void {
    this.setAuthenticationState(false);
    localStorage.removeItem('userId');
    localStorage.removeItem('userProfile');
  }

  private setAuthenticationState(authenticated: boolean): void {
    this.isAuthenticatedSubject.next(authenticated);
    localStorage.setItem('isAuthenticated', JSON.stringify(authenticated));
  }

  private startTokenRefresh(): void {
    setInterval(async () => {
      if (!this.keycloak) return;

      try {
        await this.keycloak.updateToken(this.TOKEN_EXPIRY_BUFFER);
      } catch (error) {
        console.error('Background token refresh failed:', error);
        await this.logout();
      }
    }, this.TOKEN_REFRESH_INTERVAL);
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
    return userRoles.some(role =>
      this.REQUIRED_ROLES.includes(role.toUpperCase())
    );
  }

private syncUserWithBackend(): Observable<void> {
  if (!this.keycloak?.tokenParsed) {
    console.warn('No Keycloak token available for sync');
    return of(undefined);
  }

  const userData = {
    userId: this.keycloak.tokenParsed.sub,
    username: this.keycloak.tokenParsed['preferred_username'],
    email: this.keycloak.tokenParsed['email'],
    accountStatus: 'PENDING' // Add this if needed
  };

  console.log('Sync payload:', userData); // Debug log

  return this.http.post(`${environment.userApi}/sync`, userData, {
    observe: 'response'
  }).pipe(
    tap(response => {
      console.log('Sync response status:', response.status);
      if (response.status === 202 || response.status === 200) {
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
    const userId = this.keycloak?.tokenParsed?.sub || localStorage.getItem('userId');

    if (!userId) {
      return of(false);
    }

    return this.http.get<boolean>(
      `${environment.userApi}/profile-complete/${userId}`
    ).pipe(
      catchError(error => {
        console.error('Profile completion check failed:', error);
        return of(false);
      })
    );
  }
}
