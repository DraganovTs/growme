import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { Stripe, StripeCardNumberElement, StripeCardExpiryElement, StripeCardCvcElement, loadStripe } from '@stripe/stripe-js';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NavigationExtras } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { CartService } from 'src/app/services/cart-service';
import { CheckoutService } from 'src/app/services/checkout-service';
import { ICart } from 'src/app/shared/model/cart';
import { IOrderToCreate, IAddress } from 'src/app/shared/model/order';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { CommonModule, NgIf } from '@angular/common';
import { TextInputComponent } from 'src/app/shared/components/text-input/text-input.component';

@Component({
  selector: 'app-checkout-payment',
  standalone: true,
  imports: [CommonModule, NgIf, ReactiveFormsModule,CdkStepperModule,TextInputComponent],
  templateUrl: './checkout-payment.component.html',
  styleUrl: './checkout-payment.component.scss'
})
export class CheckoutPaymentComponent {
  @Input() checkoutForm? : FormGroup;
  @ViewChild("cardNumber") cardNumberElement?: ElementRef;
  @ViewChild("cardExpiry") cardExpiryElement?: ElementRef;
  @ViewChild("cardCvc") cardCvcElement?: ElementRef;
  stripe: Stripe | null = null;
  cardNumber?: StripeCardNumberElement;
  cardExpiry?: StripeCardExpiryElement;
  cardCvc?: StripeCardCvcElement;
  cardNumberErrors: any;
  cardExpiryErrors: any;
  cardCvcErrors: any;
  cardNumberComplete = false;
  cardExpiryComplete = false;
  cardCvcComplete = false;
  
  
  constructor(private cartService: CartService, private checkoutService: CheckoutService,
    private router: Router){
  
    }
    async ngOnInit() {
      await loadStripe('pk_test_51QPpkMCAxyEgI7poxZ60BjB26DcLX3bq8cFdvB4UDTviUXTchBC6xSwUzqfTYDlUDJ0BZ5P2DqQmtuWLgQJzWuXY00CD7Q8ndH')
      .then(stripe => {
        this.stripe = stripe;
        const elements = stripe?.elements();
        if(elements){
          this.cardNumber = elements.create('cardNumber');
          this.cardNumber.mount(this.cardNumberElement?.nativeElement)
          this.cardNumber.on('change', event => {
            this.cardNumberComplete = event.complete;
            if(event.error) this.cardNumberErrors = event.error.message;
          })
  
          this.cardExpiry = elements.create('cardExpiry');
          this.cardExpiry.mount(this.cardExpiryElement?.nativeElement);
          this.cardExpiry.on('change', event => {
            this.cardExpiryComplete = event.complete;
            if(event.error) this.cardExpiryErrors = event.error.message;
          })
  
          this.cardCvc = elements.create('cardCvc');
          this.cardCvc.mount(this.cardCvcElement?.nativeElement);
          this.cardCvc.on('change', event => {
            this.cardCvcComplete = event.complete;
            if(event.error) this.cardCvcErrors = event.error.message;
          })
        }
      })
    }
  
    get paymentFormComplete(){
      return this.checkoutForm?.get('paymentForm')?.valid
      && this.cardNumberComplete
      && this.cardExpiryComplete
      && this.cardCvcComplete
    }
  
    async submitOrder(){
      const cart  = this.cartService.getCurrentCart();
      console.log("inside submit order: cart value")
      console.log(cart);
  
      try{
        const createOrder = await this.createOrder(cart);
        const paymentResult = await this.getPaymentConfirmationFromStripe(cart);
        if(paymentResult!.paymentIntent){
            this.cartService.deleteCart(cart!);
            const navigationExtras: NavigationExtras = {state:createOrder};
            this.router.navigate(['checkout/success'],navigationExtras);
        }
        else{
        }
      }
      catch(error: any){
        console.log(error);
      }
      finally{
  
      }
    }
   private async getPaymentConfirmationFromStripe(cart: ICart | null) {
      if(!cart) throw new Error(' Cart is null');
      const result = this.stripe?.confirmCardPayment(cart.clientSecret!, {
        payment_method: {
          card: this.cardNumber!,
          billing_details: {
            name: this.checkoutForm?.get('paymentForm')?.get('nameOnCard')?.value
          }
        }
      });
      if(!result){
        throw new Error("Problem whit reaching out with Stripe for confirmation");
      }
      return result;
    }
  
    private async createOrder(cart: ICart | null){
      if(!cart) throw new Error('Cart is null');
      const orderToCreate = this.getOrderToCreate(cart);
      console.log("ordertocreate object preparation");
      console.log(orderToCreate);
      return firstValueFrom(this.checkoutService.createOrder(orderToCreate));
    }
  
    private getOrderToCreate(cart: ICart): IOrderToCreate {
      const deliveryMethodId = this.checkoutForm?.get('deliveryForm')?.get('deliveryMethod')?.value;
      const shipAddress = this.checkoutForm?.get('addressForm')?.value as IAddress;
  
      console.log("Address and delivery method:");
      console.log(shipAddress);
      console.log(deliveryMethodId);
  
      if (!deliveryMethodId || !shipAddress) {
          throw new Error('There is a problem with the cart address or delivery method selection');
      }
  
      return {
          basketId: cart.id,
          deliveryMethodId: deliveryMethodId, 
          shipToAddress: shipAddress
      };
  }
  
  
}
