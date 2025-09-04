import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';


interface TokenResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  refresh_token?: string;
}

@Component({
   selector: 'app-auth-callback',
  standalone: true,
  templateUrl: './auth-callback.component.html',
  styleUrls: ['./auth-callback.component.scss']
})
export class AuthCallbackComponent implements OnInit {
  
  private readonly TOKEN_URL = 'http://localhost:7080/realms/grow-me/protocol/openid-connect/token';
  private readonly REGISTER_URL = 'http://localhost:8081/api/users/register';
  private readonly REDIRECT_URI = 'http://localhost:4200/auth/callback';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['code']) {
        this.handleAuthorizationCode(params['code']);
      }
    });
  }

  private handleAuthorizationCode(code: string): void {
    const codeVerifier = localStorage.getItem('pkce_code_verifier');
    
    if (!codeVerifier) {
      console.error('PKCE code verifier missing');
      this.router.navigate(['/login']);
      return;
    }

    this.exchangeCodeForToken(code, codeVerifier).subscribe({
      next: (tokenResponse) => this.handleTokenResponse(tokenResponse),
      error: (error) => this.handleTokenError(error)
    });
  }

  private exchangeCodeForToken(code: string, codeVerifier: string): Observable<TokenResponse> {
    const body = new URLSearchParams({
      client_id: 'grow-me-client',
      client_secret: 'NcIbcO44ILFnN87H2EFyD9BK5hx4zfkb',
      grant_type: 'authorization_code',
      code: code,
      redirect_uri: this.REDIRECT_URI,
      code_verifier: codeVerifier,
    });

    return this.http.post<TokenResponse>(this.TOKEN_URL, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).pipe(
      catchError(error => {
        console.error('Token exchange failed:', error);
        return throwError(() => new Error('Token exchange failed'));
      })
    );
  }

  private handleTokenResponse(tokenResponse: TokenResponse): void {
    if (!tokenResponse.access_token) {
      console.error('Access token missing from response');
      this.router.navigate(['/login']);
      return;
    }

    const user = this.parseJwt(tokenResponse.access_token);
    this.registerUser(user);
  }

  private registerUser(user: any): void {
    const selectedRole = localStorage.getItem('selectedRole');
    
    this.http.post(this.REGISTER_URL, {
      keycloakId: user.sub,
      username: user.preferred_username,
      email: user.email,
      role: selectedRole,
    }).subscribe({
      next: () => this.handleRegistrationSuccess(),
      error: (error) => this.handleRegistrationError(error)
    });
  }

  private handleRegistrationSuccess(): void {
    localStorage.removeItem('selectedRole');
    localStorage.removeItem('pkce_code_verifier');
    this.router.navigate(['/']);
  }

  private handleRegistrationError(error: any): void {
    console.error('User registration failed:', error);
    this.router.navigate(['/login']);
  }

  private handleTokenError(error: any): void {
    console.error('Token exchange failed:', error);
    this.router.navigate(['/login']);
  }

  private parseJwt(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (error) {
      console.error('JWT parsing failed:', error);
      return null;
    }
  }
}