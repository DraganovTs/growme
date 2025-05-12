import { CdkStepper } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';
import { CartDetailsComponent } from 'src/app/cart/cart-details/cart-details.component';
import { CartService } from 'src/app/services/cart-service';
import { ICart } from 'src/app/shared/model/cart';

@Component({
  selector: 'app-checkout-review',
  standalone: true,
  imports: [CommonModule, CartDetailsComponent],
  templateUrl: './checkout-review.component.html',
  styleUrl: './checkout-review.component.scss'
})
export class CheckoutReviewComponent {

  cart$: Observable<ICart | null> 
  @Input() insideStepper?: CdkStepper;

  constructor(private cartService: CartService){
    this.cart$ = this.cartService.cart$ 
   }

   createPaymentIntent(){
    this.cartService.createPaymentIntent()
    .subscribe({
      next: ()=> {
        this.insideStepper?.next();
      },
    })
   }
}
