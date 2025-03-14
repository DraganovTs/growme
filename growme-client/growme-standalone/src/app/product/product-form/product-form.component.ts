import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { IProduct } from '../../shared/model/product';
import { ProductService } from '../../services/product-service'
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../services/category-service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css'],
})
export class ProductFormComponent implements OnInit {
  productForm!: FormGroup;
  isEditMode = false;
  categories: any[] = []; 
  ownerId = 1; 

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadCategories();

    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.isEditMode = true;
      this.loadProduct(productId);
    }
  }

  initializeForm(): void {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      brand: [''],
      description: ['', Validators.required],
      price: [0, Validators.required],
      unitsInStock: [0, Validators.required],
      imageUrl: [''],
      categoryId: ['', Validators.required], 
      ownerName: ['John Doe', Validators.required], 
    });
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe((data) => {
      this.categories = data;
    });
  }

  loadProduct(productId: string): void {
    this.productService.getProductById(productId).subscribe((product) => {
      this.productForm.patchValue(product);
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const productData = this.productForm.value;
  
      if (this.isEditMode) {
        this.productService.updateProduct(productData.id, productData).subscribe(() => {
          this.router.navigate(['/products']);
        });
      } else {
        this.productService.addProduct(productData).subscribe(() => {
          this.router.navigate(['/products']);
        });
      }
    }
  }
  
}
