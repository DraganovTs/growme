import { CommonModule, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterModule } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';
import { CartService } from 'src/app/services/cart-service';
import { Observable } from 'rxjs';
import { ICart } from 'src/app/shared/model/cart';

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
  cart$!: Observable<ICart | null>; ;
 

  constructor(private keycloakService: KeycloakService, private cdRef: ChangeDetectorRef , private cartService: CartService) {
    this.keycloakService.isAuthenticated$.subscribe(authenticated => {
      this.isAuthenticated = authenticated;
      if (this.isAuthenticated) {
        this.username = this.keycloakService.getUsername() ?? '';
        this.isSeller = this.keycloakService.hasRole('SELLER'); 
        this.cart$ = this.cartService.cart$;
      }
    });
  }

  login() { this.keycloakService.login(); }
  logout() { this.keycloakService.logout(); }
  register() { this.keycloakService.register(); }  
}
