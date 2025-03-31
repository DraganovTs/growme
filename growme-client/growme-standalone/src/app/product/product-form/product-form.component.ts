import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { IProduct } from '../../shared/model/product';
import { ProductService } from '../../services/product-service'
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../services/category-service';
import { ImageService } from '../../services/image-service';
import { KeycloakService } from '../../services/keycloak.service';


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
  selectedImage?: File;
  selectedImageUrl?: string;
  existingImages: string[] = [];

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private categoryService: CategoryService,
    private imageService: ImageService,
    private route: ActivatedRoute,
    private router: Router,
    private keycloakService: KeycloakService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadCategories();
    this.loadExistingImages();

    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.isEditMode = true;
      this.loadProduct(productId);
    }
  }

  initializeForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      brand: ['', [Validators.required]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: [0, [Validators.required, Validators.min(0.1)]],
      unitsInStock: [0, [Validators.required, Validators.min(1)]],
      imageUrl: [''],
      categoryId: ['', Validators.required],
      ownerName: [{ value: this.keycloakService.getUsername(), disabled: true }, Validators.required], 
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
      this.selectedImageUrl = product.imageUrl;
    });
  }

  loadExistingImages(): void {
    this.imageService.getExistingImages().subscribe((images) => {
      this.existingImages = images;
    });
  }

  onFileSelected(event: any): void {
    this.selectedImage = event.target.files[0];

    if (this.selectedImage) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedImageUrl = reader.result as string;
      };
      reader.readAsDataURL(this.selectedImage);
    }
  }

  onExistingImageSelected(event: any): void {
    const selectedImage = event.target.value;
    this.productForm.patchValue({ imageUrl: selectedImage });
    this.selectedImageUrl = selectedImage;
  }

  uploadImageAndSubmit(): void {
    if (this.selectedImage) {
      this.imageService.uploadImage(this.selectedImage).subscribe((response) => {
        this.productForm.patchValue({ imageUrl: response.url });
        this.submitForm();
      });
    } else {
      this.submitForm();
    }
  }

  submitForm(): void {
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