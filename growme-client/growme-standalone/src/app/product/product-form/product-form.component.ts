import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { IProduct } from '../../shared/model/product';
import { ProductService } from '../product-service'
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.scss'
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;
  isEditMode = false;
  productId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      brand: [''],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      unitsInStock: [0, [Validators.required, Validators.min(0)]],
      imageUrl: [''],
      categoryName: ['', Validators.required],
      ownerName: ['', Validators.required],
      ownerId: ['', Validators.required],
      productCategoryId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.productId = params.get('id');
      if (this.productId) {
        this.isEditMode = true;
        this.loadProduct();
      }
    });
  }

  loadProduct() {
    if (!this.productId) return;

    this.productService.getProductById(this.productId).subscribe({
      next: product => this.productForm.patchValue(product),
      error: err => console.error('Error fetching product', err)
    });
  }

  onSubmit() {
    if (this.productForm.invalid) return;

    const product: IProduct = this.productForm.value;

    if (this.isEditMode && this.productId) {
      this.productService.updateProduct(this.productId, product).subscribe({
        next: () => this.router.navigate(['/products']),
        error: err => console.error('Error updating product', err)
      });
    } else {
      this.productService.addProduct(product).subscribe({
        next: () => this.router.navigate(['/products']),
        error: err => console.error('Error adding product', err)
      });
    }
  }
}