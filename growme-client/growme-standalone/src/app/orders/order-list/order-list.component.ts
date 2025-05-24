import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderService } from 'src/app/services/order-service';
import { IOrder } from 'src/app/shared/model/order';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent implements OnInit {
  orders: IOrder[] = [];

  constructor(private orderService: OrderService, private router: Router) {}

  ngOnInit(): void {
    this.orderService.getUserOrders().subscribe({
      next: (res) => (this.orders = res)
    });
  }

  viewOrder(orderId: string) {
    this.router.navigate(['/orders', orderId]);
  }
}
