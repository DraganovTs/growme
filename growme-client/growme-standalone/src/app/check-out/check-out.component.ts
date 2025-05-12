import { CommonModule } from '@angular/common';
import { Component,  OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ICartTotals } from '../shared/model/cart';
import { OrderTotalsComponent } from "../shared/components/order-totals/order-totals.component";
import { StepperComponent } from "../shared/components/stepper/stepper.component";
import {CdkStepperModule} from '@angular/cdk/stepper'
import { CheckoutAddressComponent } from './checkout-address/checkout-address.component';
import { CheckoutDeliveryComponent } from './checkout-delivery/checkout-delivery.component';
import { CheckoutPaymentComponent } from './checkout-payment/checkout-payment.component';
import { FormBuilder,  Validators } from '@angular/forms';
import { CheckoutReviewComponent } from "./checkout-review/checkout-review.component";
import { CartService } from '../services/cart-service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, OrderTotalsComponent, StepperComponent, CdkStepperModule,
    CheckoutAddressComponent, CheckoutDeliveryComponent, CheckoutPaymentComponent, CheckoutPaymentComponent, CheckoutReviewComponent],
  templateUrl: './check-out.component.html',
  styleUrl: './check-out.component.scss'
})
export class CheckoutComponent implements OnInit{
  cartTotals$! : Observable<ICartTotals | null>


  checkoutForm: any;

  constructor(private fb: FormBuilder, private cartService: CartService) {
    this.checkoutForm = this.fb.group({
      addressForm: this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        zipCode: ['', Validators.required]
      }),
      deliveryForm: this.fb.group({
        deliveryMethod: ['', Validators.required]
      }),
      paymentForm: this.fb.group({
        nameOnCard: ['', Validators.required]
      })
    });
  }
  

  ngOnInit(): void {
    this.cartTotals$ = this.cartService.cartTotals$;
    this.checkoutForm.markAllAsTouched();

  }

  getAddressFormValues(){
    const formValue = this.checkoutForm.get('addressForm')?.value;
    console.log(formValue);
  }

  getDeliveryMethodValue(){
    const cart = this.cartService.getCurrentCart();
    if(cart&&cart.deliveryMethodId){
      this.checkoutForm.get('deliveryForm')?.get('deliveryMethod')?.patchValue(cart.deliveryMethodId.toString());
      console.log("delivery method");
      console.log();
    }
  }
  
}
