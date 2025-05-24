import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CartDetailsComponent } from 'src/app/cart/cart-details/cart-details.component';
import { OrderService } from 'src/app/services/order-service';
import { OrderTotalsComponent } from 'src/app/shared/components/order-totals/order-totals.component';
import { ICartItem } from 'src/app/shared/model/cart';
import { IOrder } from 'src/app/shared/model/order';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule, OrderTotalsComponent, CartDetailsComponent],
  templateUrl: './order-details.component.html',
  styleUrl: './order-details.component.scss'
})
export class OrderDetailsComponent implements OnInit {


    order: IOrder | null = null;

  constructor(private route: ActivatedRoute, private orderService: OrderService) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id')!;
    this.orderService.getOrderById(orderId).subscribe({
      next: (order) => (this.order = order)
    });
  }

  get cartItems(): ICartItem[] {
  return this.order?.orderItems.map(item => ({
    productId: item.productId,
    name: item.productName, 
    unitsInStock: 0, 
    quantity: item.quantity,
    imageUrl: item.imageUrl,
    price: item.price,
    brandName: '', 
    categoryName: '', 
  })) || [];
}
}
