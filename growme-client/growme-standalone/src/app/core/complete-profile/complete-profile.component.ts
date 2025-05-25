import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environment/environments';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from '../../services/keycloak.service';
import { catchError, switchMap, of, from, throwError } from 'rxjs';

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
  isLoading = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private keycloakService: KeycloakService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    if(isPlatformBrowser(this.platformId)) {
    this.setUserInfo();
  }
}

  /** Get user info from KeycloakService */
  private setUserInfo() {
    const userId = this.keycloakService.getUserId();
    const email = this.keycloakService.getEmail();
    const username = this.keycloakService.getUsername();

     if (userId && email && username) {
      this.userProfile.id = userId;
      this.userProfile.email = email;
      this.userProfile.username = username;
    } else {
      console.warn('User information not available');
    }

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
  async submitProfile() {
    this.submitted = true;
    this.isLoading = true;

      if (!this.isFormValid()) {
      this.isLoading = false;
      return;
    }

      if (!this.isValidUUID(this.userProfile.id)) {
      console.error('Invalid user ID format');
      this.isLoading = false;
      return;
    }
  
    const apiUrl = `${environment.userApi}/update/${this.userProfile.id}`;
  

    console.log('🚀 Sending profile update:', this.userProfile); // ✅ Debug log
  
   try {
      await this.http.put(apiUrl, this.userProfile)
        .pipe(
          switchMap(() => {
            console.log('✅ Profile updated successfully!');
            // Logout after successful update
            return from(this.keycloakService.logout());
          }),
          catchError(error => {
            console.error('❌ Error updating profile:', error);
            this.errorMessage = 'Failed to update profile. Please try again.';
            this.isLoading = false;
            return throwError(() => error);
          })
        )
        .toPromise();

    } catch (error) {
      console.error('Error during profile update:', error);
      this.isLoading = false;
    }
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
    const isValid = !!this.userProfile.firstName &&
      !!this.userProfile.lastName &&
      !!this.userProfile.phone &&
      !!this.userProfile.address.street &&
      !!this.userProfile.address.city &&
      !!this.userProfile.address.state &&
      !!this.userProfile.address.zipCode &&
      this.userProfile.roles.length > 0;

    if (!isValid) {
      this.errorMessage = 'Please fill all required fields and select at least one role';
    }

    return isValid;
  }
}
