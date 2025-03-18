import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
    providedIn: 'root'
})
export class KeycloakService {
    private keycloak = new Keycloak({
        url: 'http://localhost:7080',  
        realm: 'grow-me',            
        clientId: 'grow-me-client'
          
    });

    constructor() {}

    async init(): Promise<void> {
        try {
            await this.keycloak.init({
                onLoad: 'check-sso',
                silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
                checkLoginIframe: false, 
                pkceMethod: 'S256' ,
                redirectUri: 'http://localhost:4200/'    
            });
            console.log('Keycloak Initialized:', this.keycloak.authenticated);
        } catch (error) {
            console.error('Keycloak init failed', error);
        }
    }

    login() {
        this.keycloak.login();
    }

    logout() {
        this.keycloak.logout();
    }

    getToken() {
        return this.keycloak.token;
    }

    isAuthenticated() {
        return this.keycloak.authenticated;
    }

    getUsername() {
        return this.keycloak.tokenParsed?.['preferred_username'];
    }

    async register(role: 'BUYER' | 'SELLER') {
        localStorage.setItem('selectedRole', role);
    
        const redirectUri = encodeURIComponent('http://localhost:4200/auth/callback'); 
        const codeVerifier = this.generateCodeVerifier();
        const codeChallenge = await this.generateCodeChallenge(codeVerifier);
    
       
        localStorage.setItem('pkce_code_verifier', codeVerifier);
    
        const registrationUrl = `${this.keycloak.authServerUrl}/realms/${this.keycloak.realm}/protocol/openid-connect/registrations` +
            `?client_id=${this.keycloak.clientId}` +
            `&response_type=code` +
            `&scope=openid` +
            `&redirect_uri=${redirectUri}` +
            `&code_challenge=${codeChallenge}` +
            `&code_challenge_method=S256`;
    
        window.location.href = registrationUrl;
    }
    
   
    generateCodeVerifier(): string {
        const array = new Uint8Array(32);
        crypto.getRandomValues(array);
        return btoa(String.fromCharCode.apply(null, [...array]))
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=+$/, '');
    }
    
  
    async generateCodeChallenge(codeVerifier: string): Promise<string> {
        const encoder = new TextEncoder();
        const data = encoder.encode(codeVerifier);
        const digest = await crypto.subtle.digest('SHA-256', data);
        return btoa(String.fromCharCode(...new Uint8Array(digest)))
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=+$/, '');
    }
    

    private async handleRoleAssignment() {
        const selectedRole = localStorage.getItem('selectedRole');

        if (selectedRole) {
            console.log(`Assigning role: ${selectedRole}`);
            localStorage.removeItem('selectedRole'); 

            await fetch('http://localhost:8081/users/assign-role', { 
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.getToken()}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ role: selectedRole })
            }).catch(err => console.error('Role assignment failed', err));
        }
    }
}
