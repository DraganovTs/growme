import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { KeycloakService } from 'src/app/services/keycloak.service';

@Component({
  selector: 'app-bid-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bid-form.component.html',
  styleUrl: './bid-form.component.scss'
})
export class BidFormComponent {
  @Input() taskId!: string;
  @Output() bidSubmitted = new EventEmitter<any>();
  @Output() formCanceled = new EventEmitter<void>();

  bidForm: FormGroup;
  submitting = false;
  minHarvestDate: string;

  constructor(
    private fb: FormBuilder,
    private keycloakService: KeycloakService
  ) {
    // Set minimum harvest date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minHarvestDate = tomorrow.toISOString().split('T')[0];

    this.bidForm = this.fb.group({
      price: ['', [Validators.required, Validators.min(0.01)]],
      proposedHarvestDate: [this.minHarvestDate, Validators.required],
      deliveryMethod: ['delivery', Validators.required],
      deliveryIncluded: [false],
      message: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(500)]]
    });
  }

  onSubmit() {
    if (this.bidForm.invalid || this.submitting) return;

    this.submitting = true;
    const formValue = this.bidForm.value;

    const bidData = {
      ...formValue,
      taskId: this.taskId,
      growerId: this.keycloakService.getUserId(),
      growerName: this.keycloakService.getUsername() || 'Anonymous Grower'
    };

    this.bidSubmitted.emit(bidData);
  }

  onCancel() {
    this.formCanceled.emit();
  }

  get price() { return this.bidForm.get('price'); }
  get message() { return this.bidForm.get('message'); }
  get proposedHarvestDate() { return this.bidForm.get('proposedHarvestDate'); }
  get deliveryMethod() { return this.bidForm.get('deliveryMethod'); }
}
