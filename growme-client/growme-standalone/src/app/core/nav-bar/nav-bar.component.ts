import { CommonModule, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterModule } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [RouterLinkActive , CommonModule , RouterModule , RouterLinkActive , RouterLink , NgIf ],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss'
})
export class NavBarComponent {
  isAuthenticated = false;
  username:  string = '';
  showRegisterModal: boolean = false;

  constructor(private keycloakService: KeycloakService) {
      this.isAuthenticated = !!this.keycloakService.isAuthenticated();
      if (this.isAuthenticated) {
          this.username = this.keycloakService.getUsername();
      }
  }

  login() { this.keycloakService.login(); }
  logout() { this.keycloakService.logout(); }
  showRegisterOptions() {
    this.showRegisterModal = true; 
}

register(role: 'BUYER' | 'SELLER') {
    this.keycloakService.register(role);
    this.showRegisterModal = false; 
}
}
