import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environment/environments';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complete-profile.component.html',
  styleUrl: './complete-profile.component.scss'
})
export class CompleteProfileComponent {
  userProfile = {
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

  toggleRole(role: string) {
    const index = this.userProfile.roles.indexOf(role);
    if (index === -1) {
      this.userProfile.roles.push(role); 
    } else {
      this.userProfile.roles.splice(index, 1);
    }
  }

  submitProfile() {
    console.log('Updated Profile:', this.userProfile);
  }
}