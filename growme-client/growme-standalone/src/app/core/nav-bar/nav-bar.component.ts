import { CommonModule, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterModule } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [RouterLinkActive, CommonModule, RouterModule, RouterLink, NgIf],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss'
})
export class NavBarComponent {
  isAuthenticated = false;
  isSeller = false;
  username: string = '';
 

  constructor(private keycloakService: KeycloakService, private cdRef: ChangeDetectorRef) {
    this.keycloakService.isAuthenticated$.subscribe(authenticated => {
      this.isAuthenticated = authenticated;
      if (this.isAuthenticated) {
        this.username = this.keycloakService.getUsername() ?? '';
        this.isSeller = this.keycloakService.hasRole('SELLER'); 

      }
    });
  }

  login() { this.keycloakService.login(); }
  logout() { this.keycloakService.logout(); }
  register() { this.keycloakService.register(); }  
}
