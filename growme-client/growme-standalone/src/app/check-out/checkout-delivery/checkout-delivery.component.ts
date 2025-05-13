import { CdkStepperModule } from '@angular/cdk/stepper';
import { CommonModule, NgIf, NgFor, CurrencyPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { CartService } from 'src/app/services/cart-service';
import { CheckoutService } from 'src/app/services/checkout-service';
import { IDeliveryMethod } from 'src/app/shared/model/delivery';

@Component({
  selector: 'app-checkout-delivery',
  standalone: true,
  imports: [CommonModule, NgIf,NgFor,ReactiveFormsModule,CurrencyPipe, CdkStepperModule],
  templateUrl: './checkout-delivery.component.html',
  styleUrl: './checkout-delivery.component.scss'
})
export class CheckoutDeliveryComponent {

  @Input() checkoutForm? : FormGroup;
  deliveryMethods: IDeliveryMethod[] = [];
  constructor(private checkoutService: CheckoutService, private cartService: CartService){

  }

  ngOnInit(): void {
    console.log('Initializing CheckoutDeliveryComponent');
    if (!this.checkoutForm) {
      console.error('checkoutForm is missing in CheckoutDeliveryComponent');
      return;
    }
  
    console.log('Fetching delivery methods...');
    this.checkoutService.getDeliveryMethods().subscribe(
      (dm: IDeliveryMethod[]) => {
        console.log('Received delivery methods:', dm);
        this.deliveryMethods = dm;
      },
      error => {
        console.error('Error fetching delivery methods:', error);
      }
    );
  }

  setShippingPrice(deliveryMethod: IDeliveryMethod) {
    console.log("Selected Delivery Method:", deliveryMethod);
    console.log("Setting Form Value:", deliveryMethod.deliveryMethodId);
    
    this.cartService.setShippingPrice(deliveryMethod);
    this.checkoutForm?.get('deliveryForm')?.get('deliveryMethod')?.setValue(deliveryMethod.deliveryMethodId);
    
    console.log("Updated Form Value:", this.checkoutForm?.get('deliveryForm')?.get('deliveryMethod')?.value);
  }
}
