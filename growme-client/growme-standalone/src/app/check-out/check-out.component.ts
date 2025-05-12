import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ICartTotals } from '../shared/model/cart';
import { CartService } from '../services/cart-service';
import { OrderTotalsComponent } from "../shared/components/order-totals/order-totals.component";

@Component({
  selector: 'app-check-out',
  imports: [OrderTotalsComponent],
  templateUrl: './check-out.component.html',
  styleUrl: './check-out.component.scss'
})
export class CheckOutComponent implements OnInit {
cartTotals$! : Observable<ICartTotals | null>;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.cartTotals$ = this.cartService.cartTotals$;
  }


}
