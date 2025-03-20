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
      const authenticated = await this.keycloak.init({ onLoad: 'check-sso', checkLoginIframe: false });
      this.isAuthenticatedSubject.next(authenticated);
      localStorage.setItem('isAuthenticated', JSON.stringify(authenticated));
      
      if (authenticated && this.keycloak.tokenParsed) {
        const userInfo = {
          userId: this.keycloak.tokenParsed.sub,
          username: this.keycloak.tokenParsed['username'],
          email: this.keycloak.tokenParsed['email'],
          roles: this.keycloak.tokenParsed.realm_access?.roles || [], 
        };
        
        this.syncUserToBackend(userInfo);
        this.startTokenRefresh();
      }
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      this.isAuthenticatedSubject.next(false);
      localStorage.removeItem('isAuthenticated');
    }
  }

  private syncUserToBackend(userInfo: any) {
    this.http.post(`${environment.userApi}`, userInfo).subscribe({
      next: () => console.log('User synced successfully'),
      error: (error) => console.error('Failed to sync user:', error),
    });
  }

  async logout(): Promise<void> {
    if (this.keycloak) {
      await this.keycloak.logout();
      this.isAuthenticatedSubject.next(false);
      localStorage.removeItem('isAuthenticated');
      this.router.navigate(['/login']);
    }
  }

  getUsername(): string | null {
    return this.keycloak?.tokenParsed?.['preferred_username'] || null;
  }

  async login(): Promise<void> {
    await this.keycloak?.login();
  }

  async register(role?: string): Promise<void> {
    await this.keycloak?.register();
  }

  private startTokenRefresh(): void {
    setInterval(async () => {
      try {
        await this.keycloak?.updateToken(60);
      } catch (error) {
        console.error('Failed to refresh token', error);
        this.logout();
      }
    }, 60000);
  }
}