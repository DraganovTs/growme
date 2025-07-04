import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { CategoryService } from 'src/app/services/category-service';
import { KeycloakService } from 'src/app/services/keycloak.service';
import { TaskService } from 'src/app/services/task-service';
import { ICategory } from 'src/app/shared/model/product';

@Component({
  selector: 'app-create-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-task.component.html',
  styleUrl: './create-task.component.scss',
  providers: [
    TaskService,
    ToastrService,
    CategoryService
  ]
})
export class CreateTaskComponent implements OnInit{
  taskForm!: FormGroup;
  submitting = false;
  minHarvestDate: string;
  productTypes: ICategory[] = [];
  qualityStandards = [
    { value: 'organic', label: 'Organic Certified' },
    { value: 'pesticideFree', label: 'Pesticide Free' },
    { value: 'conventional', label: 'Conventional' },
    { value: 'other', label: 'Other' }
  ];
  units = [
    { value: 'kg', label: 'Kilograms' },
    { value: 'g', label: 'Grams' },
    { value: 'lb', label: 'Pounds' },
    { value: 'oz', label: 'Ounces' },
    { value: 'bunch', label: 'Bunch' },
    { value: 'piece', label: 'Piece' }
  ];
  deliveryMethods = [
    { value: 'pickup', label: 'Pickup from farm' },
    { value: 'delivery', label: 'Delivery to my location' },
    { value: 'market', label: 'Meet at local farmers market' },
    { value: 'flexible', label: 'Flexible - will discuss with grower' }
  ];
  priceModels = [
    { value: 'fixed', label: 'Fixed Price' },
    { value: 'negotiable', label: 'Negotiable' },
    { value: 'market', label: 'Market Price at Harvest' }
  ];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private keycloakService: KeycloakService,
    private router: Router,
    private toastr: ToastrService,
    private categoryService: CategoryService
  ) {
    // Set min harvest date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minHarvestDate = tomorrow.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.initForm();
    this.checkAuthentication();
    this.loadCategories();
  }

    private loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.productTypes = data;
      },
      error: (err) => {
        console.error('Failed to load categories', err);
        this.toastr.error('Failed to load product categories. Please try again.');
      }
    });
  }

  private checkAuthentication(): void {
    if (!this.keycloakService.getUserId()) {
      this.toastr.warning('Please login to create a grow task');
      this.router.navigate(['/login']);
    }
  }

  initForm(): void {
  this.taskForm = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.required, Validators.maxLength(1000)]],
    categoryName: ['', Validators.required], // Changed from productType to categoryName
    quantity: ['', [Validators.required, Validators.min(1), Validators.max(10000)]],
    unit: ['kg', Validators.required],
    quality: ['organic', Validators.required],
    harvestDate: ['', Validators.required],
    deliveryDate: ['', Validators.required],
    flexibleDates: [false],
    deliveryLocation: ['', [Validators.required, Validators.maxLength(100)]],
    deliveryMethod: ['flexible', Validators.required], // Added required validator
    willingToShip: [false],
    maxPrice: ['', [Validators.min(0), Validators.max(10000)]], // Will be mapped to budget
    priceModel: ['negotiable', Validators.required], // Added required validator
    photosRequired: [false],
    visitFarm: [false]
  });

    // Set delivery date to be after harvest date
    this.taskForm.get('harvestDate')?.valueChanges.subscribe(harvestDate => {
      if (harvestDate) {
        const harvestDateObj = new Date(harvestDate);
        const minDeliveryDate = new Date(harvestDateObj);
        minDeliveryDate.setDate(harvestDateObj.getDate() + 1);

            const minDeliveryDateString = minDeliveryDate.toISOString().split('T')[0];

        
      this.taskForm.get('deliveryDate')?.setValidators([
      Validators.required,
      // Create a custom validator to compare dates properly
      (control) => {
        if (!control.value) return null;
        return control.value >= minDeliveryDateString ? null : { min: true };
      }
    ]);
    this.taskForm.get('deliveryDate')?.updateValueAndValidity();
      }
    });
  }

  onSubmit(): void {
    if (this.taskForm.invalid) {
      this.markFormGroupTouched(this.taskForm);
      this.toastr.error('Please fill all required fields correctly');
      return;
    }

    this.submitting = true;
    
    const formValue = this.taskForm.value;
      const taskData = {
    title: formValue.title,
    description: formValue.description,
    categoryName: formValue.categoryName,
    quantity: formValue.quantity,
    unit: formValue.unit,
    quality: formValue.quality,
    harvestDate: formValue.harvestDate,
    deliveryDate: formValue.deliveryDate,
    flexibleDates: formValue.flexibleDates,
    deliveryLocation: formValue.deliveryLocation,
    deliveryMethod: formValue.deliveryMethod,
    willingToShip: formValue.willingToShip,
    budget: formValue.maxPrice || 0, 
    priceModel: formValue.priceModel,
    photosRequired: formValue.photosRequired,
    visitFarm: formValue.visitFarm,
    status: 'OPEN' // Default status
  };

    this.taskService.createTask(taskData).subscribe({
      next: (response) => {
        this.submitting = false;
        this.toastr.success('Grow task created successfully!');
        this.router.navigate(['/tasks', response.id]);
      },
      error: (error) => {
        console.error('Error creating task:', error);
        this.submitting = false;
        this.toastr.error('Failed to create grow task. Please try again.');
      }
    });
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  cancel(): void {
    if (this.taskForm.dirty) {
      if (confirm('Are you sure you want to cancel? All unsaved changes will be lost.')) {
        this.router.navigate(['/tasks']);
      }
    } else {
      this.router.navigate(['/tasks']);
    }
  }
}