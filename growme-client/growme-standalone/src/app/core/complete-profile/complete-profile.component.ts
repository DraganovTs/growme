import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment/environments';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complete-profile.component.html',
  styleUrl: './complete-profile.component.scss'
})
export class CompleteProfileComponent {
  userProfile = {
    id: '',
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    address: {
      street: '',
      city: '',
      state: '',
      zipCode: '',
    },
    roles: [] as string[],
  };

  availableRoles: string[] = ['Buyer', 'Seller'];
  submitted = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private keycloakService: KeycloakService
  ) {
    this.setUserInfo();
  }

  /** Get user info from KeycloakService */
  private setUserInfo() {
    const userId = this.keycloakService.getUserId();
    const email = this.keycloakService.getEmail();
    const username = this.keycloakService.getUsername();
  
    if (userId) {
      this.userProfile.id = userId;
    } else {
      console.error('User ID not found');
    }
  
    if (email) {
      this.userProfile.email = email;
    } else {
      console.warn('Email not found in Keycloak');
    }
  
    if (username) {
      this.userProfile.username = username;
    } else {
      console.warn('Username not found in Keycloak');
    }
  }

  toggleRole(role: string) {
    const index = this.userProfile.roles.indexOf(role);
    if (index === -1) {
      this.userProfile.roles.push(role); 
    } else {
      this.userProfile.roles.splice(index, 1);
    }
  }

  submitProfile() {
    this.submitted = true;
    if (!this.isFormValid()) return;
  
    const apiUrl = `${environment.userApi}/update/${this.userProfile.id}`;
  
    this.http.put(apiUrl, this.userProfile).subscribe({
      next: () => {
        alert('Profile updated successfully!');
        this.router.navigate(['/home']); 
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        alert('Failed to update profile. Please try again.');
      },
    });
  }

  private isFormValid(): boolean {
    return (
      !!this.userProfile.firstName &&
      !!this.userProfile.lastName &&
      !!this.userProfile.phone &&
      !!this.userProfile.address.street &&
      !!this.userProfile.address.city &&
      !!this.userProfile.address.state &&
      !!this.userProfile.address.zipCode &&
      this.userProfile.roles.length > 0
    );
  }
  
}
