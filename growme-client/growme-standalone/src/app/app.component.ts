import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { NavigationStart, Router, RouterOutlet } from '@angular/router';
import { CartService } from './services/cart-service'; 
import { NavBarComponent } from './core/nav-bar/nav-bar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavBarComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  showNavbar = true;

  constructor(
    private router: Router,
    private cartService: CartService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.showNavbar = !event.url.includes('/auth');
      }
    });
  }

  ngOnInit(): void {
    this.loadBasket();
  }

  loadBasket() {
    if (isPlatformBrowser(this.platformId)) {
      const cartId = localStorage.getItem('angular_cart_id');
      if (cartId) {
        this.cartService.getCart(cartId).subscribe({
          next: () => console.log('Cart loaded successfully'),
          error: (err) => console.error('Error loading cart:', err)
        });
      }
    }
  }
}