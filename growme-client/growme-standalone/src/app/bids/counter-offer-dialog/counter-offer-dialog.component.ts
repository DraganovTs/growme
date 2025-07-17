import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-counter-offer-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './counter-offer-dialog.component.html',
  styleUrls: ['./counter-offer-dialog.component.scss']
})
export class CounterOfferDialogComponent {
counterForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CounterOfferDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { bidId: string }
  ) {
    this.counterForm = this.fb.group({
      price: ['', [Validators.required, Validators.min(0.01)]],
      message: ['', Validators.required]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.counterForm.valid) {
      this.dialogRef.close(this.counterForm.value);
    }
  }
}