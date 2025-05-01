import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CartService } from '../services/cart-service';
import { ICart, ICartTotals, ICartItem } from '../shared/model/cart';
import { CartDetailsComponent } from "./cart-details/cart-details.component";

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, CartDetailsComponent],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cart$!: Observable<ICart |null>;
  cartTotals$!: Observable<ICartTotals | null>;

  constructor(public cartService: CartService) {

  }

  ngOnInit(): void {
       this.cart$ = this.cartService.cart$;
       this.cartTotals$ = this.cartService.cartTotals$;
  }
   
    incrementItemQuantity(item: ICartItem) {
      this.cartService.incrementItemQuantity(item);
    }
    decrementItemQuantity(item: ICartItem) {
      this.cartService.decrementItemQuantity(item);
    }
    removeCartItem(item: ICartItem) {
      this.cartService.removeItemFromCart(item);
    }
}
