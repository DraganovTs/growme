import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-order-totals',
  standalone: true,
  imports: [CommonModule,CurrencyPipe],
  templateUrl: './order-totals.component.html',
  styleUrl: './order-totals.component.scss'
})
export class OrderTotalsComponent {
  @Input() shippingPrice: number = 0;
  @Input() subtotal: number =0;
  @Input() total: number =0;

}
