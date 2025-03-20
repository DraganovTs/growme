import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../environment/environments';
import Keycloak from 'keycloak-js';
import { isPlatformBrowser } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class KeycloakService {
  private keycloak: Keycloak | null = null;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private isBrowser: boolean; 

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object 
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    if (this.isBrowser) {
      const storedAuth = localStorage.getItem('isAuthenticated');
      if (storedAuth) {
        this.isAuthenticatedSubject.next(JSON.parse(storedAuth));
      }
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
      const authenticated = await this.keycloak.init({ onLoad: 'check-sso', checkLoginIframe: false });
      this.isAuthenticatedSubject.next(authenticated);
      
      if (this.isBrowser) {
        localStorage.setItem('isAuthenticated', JSON.stringify(authenticated));
      }
  
      if (authenticated && this.keycloak.tokenParsed) {
        console.log('User is logged in');
  
        
        const userInfo = {
            userId: this.keycloak.tokenParsed.sub,
            username: this.keycloak.tokenParsed['preferred_username'],
            email: this.keycloak.tokenParsed['email'],
            roles: this.keycloak.tokenParsed.realm_access?.roles || [], 
          };
  
        this.syncUserToBackend(userInfo);
        this.startTokenRefresh();
      } else {
        console.log('User is NOT logged in');
      }
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      this.isAuthenticatedSubject.next(false);
      if (this.isBrowser) {
        localStorage.removeItem('isAuthenticated');
      }
    }
  }
  
  private syncUserToBackend(userInfo: any) {
    this.http.post(`${environment.userApi}`, userInfo).subscribe({
      next: (response) => console.log('User synced successfully:', response),
      error: (error) => console.error('Failed to sync user:', error),
    });
  }

  getToken(): string | null {
    return this.keycloak?.token || null;
  }

  getUserRoles(): string[] {
    return this.keycloak?.tokenParsed?.realm_access?.roles || [];
  }

  async logout(): Promise<void> {
    if (this.keycloak) {
      try {
        await this.keycloak.logout();
        this.isAuthenticatedSubject.next(false);
        if (this.isBrowser) {
          localStorage.removeItem('isAuthenticated');
        }
        this.router.navigate(['/login']);
      } catch (error) {
        console.error('Logout failed', error);
      }
    }
  }

  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  getUsername(): string | null {
    return this.keycloak?.tokenParsed?.['preferred_username'] || null;
  }

  async login(): Promise<void> {
    if (this.keycloak) {
      await this.keycloak.login();
    }
  }

  async register(role?: string): Promise<void> {
    if (this.keycloak) {
      await this.keycloak.register(); 
    }
  }

  private startTokenRefresh(): void {
    if (!this.keycloak) return;

    setInterval(async () => {
      try {
        if (this.keycloak) {
          await this.keycloak.updateToken(60); 
          console.log('Token refreshed successfully');
        }
      } catch (error) {
        console.error('Failed to refresh token', error);
        this.logout();
      }
    }, 60000);
  }
}
