import { CdkStepperModule } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TextInputComponent } from 'src/app/shared/components/text-input/text-input.component';

@Component({
  selector: 'app-checkout-address',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, TextInputComponent, RouterLink,CdkStepperModule],
  templateUrl: './checkout-address.component.html',
  styleUrl: './checkout-address.component.scss'
})
export class CheckoutAddressComponent {
  @Input() checkoutForm! : FormGroup;

}
