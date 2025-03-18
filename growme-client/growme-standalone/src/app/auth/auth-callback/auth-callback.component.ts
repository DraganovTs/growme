import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [],
  templateUrl: './auth-callback.component.html',
  styleUrl: './auth-callback.component.scss'
})
export class AuthCallbackComponent {
  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      if (params['code']) {
        const code = params['code'];
        const codeVerifier = localStorage.getItem('pkce_code_verifier');

        if (!codeVerifier) {
          console.error('Missing PKCE verifier');
          return;
        }


        this.http.post<any>('http://localhost:7080/realms/grow-me/protocol/openid-connect/token',
          new URLSearchParams({
            client_id: 'grow-me-client',
            client_secret: 'YS8RI2mJMexWH6ooujvN1SO0KxYOx1pg',  
            grant_type: 'authorization_code',
            code: code,
            redirect_uri: 'http://localhost:4200/auth/callback',  
            code_verifier: codeVerifier, 
          }).toString(),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          }
        ).subscribe(
          tokenResponse => {
            console.log('Token received:', tokenResponse);

            const accessToken = tokenResponse.access_token;
            if (!accessToken) {
              console.error('Access token is missing from the response');
              return;
            }

  
            const user = this.parseJwt(accessToken);
            console.log('User:', user);

          
            this.http.post('http://localhost:8080/api/users/register', {
              keycloakId: user.sub,
              username: user.preferred_username,
              email: user.email,
              role: localStorage.getItem('selectedRole'),
            }).subscribe(() => {
              console.log('User saved to backend');
              localStorage.removeItem('selectedRole');
              this.router.navigate(['/']); 
            });
          },
          error => {
            console.error('Token exchange failed:', error);
          }
        );
      }
    });
  }

  parseJwt(token: string): any {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      console.error('Invalid token format', e);
      return null;
    }
  }
}
