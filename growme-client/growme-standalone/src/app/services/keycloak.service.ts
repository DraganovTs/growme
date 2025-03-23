import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
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
  
        const accountStatus = await this.syncUserToBackend(userInfo);
  
        localStorage.setItem('userId', userInfo.userId);
  
        console.log("Account Status:", accountStatus); 
  
        if (accountStatus === 'PENDING') {
            console.log("Redirecting to complete profile...");
            
            this.ngZone.run(() => {
                console.log("Inside NgZone, navigating now...");

              this.router.navigate(['/complete-profile']).then(success => {
                console.log('Navigation success:', success);
              }).catch(err => {
                console.error('Navigation error:', err);
              });
            });
          }
          
  
        this.startTokenRefresh();
      }
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      this.isAuthenticatedSubject.next(false);
      localStorage.removeItem('isAuthenticated');
    }
  }


  private extractUserInfo(): any {
    if (!this.keycloak?.tokenParsed) return null;

    return {
      userId: this.keycloak.tokenParsed.sub,
      username: this.keycloak.tokenParsed['preferred_username'],
      email: this.keycloak.tokenParsed['email'],
    };
  }

  private async syncUserToBackend(userInfo: any): Promise<string> {
    console.log("Syncing user to backend with data:", userInfo);
  
    return this.http.post<{ accountStatus?: string }>(`${environment.userApi}/sync`, userInfo)
      .toPromise()
      .then(response => {
        console.log("Backend Response:", response);
        return response?.accountStatus ?? 'PENDING';
      })
      .catch(error => {
        console.error('Failed to sync user:', error);
        return 'PENDING';
      });
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
    return from(this.keycloak?.register() || Promise.resolve());
  }

  getUserId(): string | null {
    return this.keycloak?.tokenParsed?.sub || localStorage.getItem('userId') || null;
  }

  getEmail(): string {
    return this.keycloak?.tokenParsed?.['email'] ?? '';
  }
  
}
