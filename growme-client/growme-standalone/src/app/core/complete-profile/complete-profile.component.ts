import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment/environments';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../../services/keycloak.service';
import { catchError, switchMap, of } from 'rxjs';

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
  errorMessage: string = '';

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

    this.userProfile.id = userId || '';
    this.userProfile.email = email || '';
    this.userProfile.username = username || '';

    if (!userId) console.error('User ID not found');
    if (!email) console.warn('Email not found in Keycloak');
    if (!username) console.warn('Username not found in Keycloak');
  }

  toggleRole(role: string) {
    const index = this.userProfile.roles.indexOf(role);
    if (index === -1) {
      this.userProfile.roles.push(role);
    } else {
      this.userProfile.roles.splice(index, 1);
    }
  }

  /** Submit profile and sync user with backend */
  submitProfile() {
    this.submitted = true;
    if (!this.isFormValid()) return;
  
    const apiUrl = `${environment.userApi}/update/${this.userProfile.id}`;
  
     if (!this.isValidUUID(this.userProfile.id)) {
    console.error('Invalid user ID format');
    return;
  }

    console.log('🚀 Sending profile update:', this.userProfile); // ✅ Debug log
  
    this.http.put(apiUrl, this.userProfile).subscribe({
      next: () => {
        console.log('✅ Profile updated successfully!');
      
  
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('❌ Error updating profile:', error);
        alert('Failed to update profile. Please try again.');
      },
    });
  }
  
  private isValidUUID(id: string): boolean {
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
  return uuidRegex.test(id);
}
  

  /** Ensure the user is synced with the backend after updating */
  private syncUserWithBackend() {
    const syncUrl = `${environment.userApi}/sync`;
    const syncData = {
      userId: this.userProfile.id,
      username: this.userProfile.username,
      email: this.userProfile.email,
      firstName: this.userProfile.firstName,
      lastName: this.userProfile.lastName,
      roles: this.userProfile.roles,
    };
  
    console.log('🛠️ Preparing to sync user with backend:', syncData); // ✅ Debug log
  
    this.http.post(syncUrl, syncData).subscribe({
      next: () => console.log('✅ User synced successfully!'),
      error: (error) => console.error('❌ Sync failed:', error),
    });
  }
  
  

  /** Validate required fields */
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
