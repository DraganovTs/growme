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
import { KeycloakService } from 'src/app/services/keycloak.service';

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
    private router: Router, private keycloakService: KeycloakService) {
  
    }
    async ngAfterViewInit() {
      await this.loadStripeElements();
    }
  
    private async loadStripeElements() {
      const stripe = await loadStripe('pk_test_51QPpkMCAxyEgI7poxZ60BjB26DcLX3bq8cFdvB4UDTviUXTchBC6xSwUzqfTYDlUDJ0BZ5P2DqQmtuWLgQJzWuXY00CD7Q8ndH');
      if (!stripe) throw new Error('Stripe failed to load');
  
      this.stripe = stripe;
      const elements = stripe.elements();
  
      this.setupCardElement(elements, 'cardNumber', 'cardNumberElement', (event) => {
        this.cardNumberComplete = event.complete;
        this.cardNumberErrors = event.error?.message;
      });
  
      this.setupCardElement(elements, 'cardExpiry', 'cardExpiryElement', (event) => {
        this.cardExpiryComplete = event.complete;
        this.cardExpiryErrors = event.error?.message;
      });
  
      this.setupCardElement(elements, 'cardCvc', 'cardCvcElement', (event) => {
        this.cardCvcComplete = event.complete;
        this.cardCvcErrors = event.error?.message;
      });
    }
  
    private setupCardElement(
      elements: any,
      elementType: 'cardNumber' | 'cardExpiry' | 'cardCvc',
      elementRef: 'cardNumberElement' | 'cardExpiryElement' | 'cardCvcElement',
      onChange: (event: any) => void
    ) {
      const element = elements.create(elementType);
      element.mount(this[elementRef]?.nativeElement);
      element.on('change', onChange);
      this[elementType] = element;
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
          shipToAddress: shipAddress,
          userEmail: this.keycloakService.getEmail()
  }
  
  
}

}
