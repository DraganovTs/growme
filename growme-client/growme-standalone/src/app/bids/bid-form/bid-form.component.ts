import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Bid } from 'src/app/shared/model/bid';

@Component({
  selector: 'app-bid-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bid-form.component.html',
  styleUrl: './bid-form.component.scss'
})
export class BidFormComponent {
  @Input() taskId!: string;
  @Output() bidSubmitted = new EventEmitter<Bid>();
  
  bidForm: FormGroup;
  
  constructor(private fb: FormBuilder) {
    this.bidForm = this.fb.group({
      price: ['', [Validators.required, Validators.min(0.01)]],
      message: ['', [Validators.required, Validators.minLength(20)]],
      // ... other form controls
    });
  }
  
  onSubmit() {
    if (this.bidForm.valid) {
      this.bidSubmitted.emit({
        ...this.bidForm.value,
        taskId: this.taskId
      });
    }
  }
}
