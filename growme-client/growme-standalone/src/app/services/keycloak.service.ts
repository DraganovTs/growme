import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, from, of, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { environment } from '../environment/environments';
import Keycloak from 'keycloak-js';
import { isPlatformBrowser } from '@angular/common';
import { NgZone } from '@angular/core';


@Injectable({ providedIn: 'root' })
export class KeycloakService {
  private keycloak: Keycloak | null = null;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    private ngZone: NgZone,
    @Inject(PLATFORM_ID) private platformId: object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    if (this.isBrowser) {
      this.isAuthenticatedSubject.next(JSON.parse(localStorage.getItem('isAuthenticated') || 'false'));
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
        checkLoginIframe: false
      });
  
      this.isAuthenticatedSubject.next(authenticated);
      localStorage.setItem('isAuthenticated', JSON.stringify(authenticated));
  
      if (authenticated && this.keycloak.tokenParsed) {
        const userInfo = {
          userId: this.keycloak.tokenParsed.sub || '',
          username: this.keycloak.tokenParsed['preferred_username'] || '',
          email: this.keycloak.tokenParsed['email'] || '',
        };
  
        const userRoles = this.keycloak.tokenParsed.realm_access?.roles || [];
      console.log("User Roles:", userRoles);

      localStorage.setItem('userId', userInfo.userId);

      this.syncUserWithBackend().subscribe(() => {
        if (!this.hasRequiredRole(userRoles)) {
          console.log("Redirecting to complete profile...");
          this.ngZone.run(() => {
            this.router.navigate(['/complete-profile']).then(success => {
              console.log('Navigation success:', success);
            }).catch(err => {
              console.error('Navigation error:', err);
            });
          });
        }
      });

      this.startTokenRefresh();
    }
  } catch (error) {
    console.error('Keycloak initialization failed:', error);
    this.isAuthenticatedSubject.next(false);
    localStorage.removeItem('isAuthenticated');
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


  async logout(): Promise<void> {
    if (this.keycloak) {
      await this.keycloak.logout();
      this.setAuthenticationState(false);
      this.router.navigate(['/login']);
    }
  }


  private startTokenRefresh(): void {
    setInterval(async () => {
      if (!this.keycloak) return;
      try {
        await this.keycloak.updateToken(60);
      } catch (error) {
        console.error('Failed to refresh token', error);
        this.logout();
      }
    }, 60000);
  }

 
  private setAuthenticationState(authenticated: boolean): void {
    this.isAuthenticatedSubject.next(authenticated);
    localStorage.setItem('isAuthenticated', JSON.stringify(authenticated));
  }

 
  getUsername(): string | null {
    return this.keycloak?.tokenParsed?.['preferred_username'] || null;
  }

  
  login(): Observable<void> {
    return from(this.keycloak?.login() || Promise.resolve());
  }


  register(): Observable<void> {
    if (!this.keycloak) {
      return of(undefined);
    }

    return from(this.keycloak.register()).pipe(
      switchMap(() => from(this.keycloak!.init({ onLoad: 'check-sso', checkLoginIframe: false }))),
      switchMap(authenticated => {
        if (!authenticated || !this.keycloak?.tokenParsed) {
          throw new Error('Authentication failed');
        }

        const userRoles = this.keycloak.tokenParsed.realm_access?.roles || [];
        this.updateAuthState(true);

        console.log("📡 Calling syncUserWithBackend()...");


        return this.syncUserWithBackend().pipe(
          switchMap(() => {
            const redirectUrl = this.hasRequiredRole(userRoles) ? '/dashboard' : '/update';
            return from(this.ngZone.run(() => this.router.navigate([redirectUrl])));
          }),
          map(() => void 0)
        );
      }),
      catchError(error => {
        console.error('Registration failed:', error);
        this.updateAuthState(false);
        return of(void 0);
      })
    );
  }
  
  
  
  private updateAuthState(isAuthenticated: boolean): void {
    this.isAuthenticatedSubject.next(isAuthenticated);
    localStorage.setItem('isAuthenticated', JSON.stringify(isAuthenticated));
  }

  getUserId(): string | null {
    if (this.keycloak?.tokenParsed?.sub) {
      return this.keycloak.tokenParsed.sub;
    }

    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('userId');
    }

    return null;
  }

  getEmail(): string {
    return this.keycloak?.tokenParsed?.['email'] ?? '';
  }
  
  private hasRequiredRole(userRoles: string[]): boolean {
    const requiredRoles = ['SELLER', 'BUYER', 'ADMIN'];
    return userRoles.some(role => requiredRoles.includes(role.toUpperCase()));
  }

  private syncUserWithBackend(): Observable<any> {
    if (!this.keycloak?.tokenParsed) {
      console.warn("No Keycloak token parsed, skipping sync.");
      return of(null);
    }
  
    const userData = {
      userId: this.keycloak.tokenParsed.sub,
      username: this.keycloak.tokenParsed['preferred_username'],
      email: this.keycloak.tokenParsed['email'],
    };
  
    console.log("Syncing user with backend:", userData);
    console.log(`API Endpoint: ${environment.userApi}/sync`);
  
    return this.http.post(`${environment.userApi}/sync`, userData, { 
      observe: 'response' 
  }).pipe(
      map(response => {
          if (response.status === 202) {
              console.log('Sync successful');
              return undefined;
          }
          throw new Error('Unexpected response');
      }),
        catchError(error => {
          console.error('Sync failed', error);
          this.router.navigate(['/register']); 
          return throwError(() => new Error('Sync failed'));
      })
  );
  }
  
  

  hasRole(role: string): boolean {
    return this.keycloak?.tokenParsed?.realm_access?.roles?.includes(role) ?? false;
  }
}
