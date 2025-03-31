import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { IProduct } from '../../shared/model/product';
import { ProductService } from '../../services/product-service'
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../services/category-service';
import { ImageService } from '../../services/image-service';
import { KeycloakService } from '../../services/keycloak.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { finalize } from 'rxjs';
import { FilenamePipe } from './Filename.Pipe';


@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,FormsModule,FilenamePipe],
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css'],
})
export class ProductFormComponent implements OnInit {
  productForm!: FormGroup;
  isEditMode = false;
  isSubmitting = false;
  categories: any[] = [];
  ownerId = 1;
  
  // Image handling
  selectedImageFile: File | null = null;
  selectedImageUrl: SafeUrl | string | null = null;
  existingImages: string[] = [];
  filteredImages: string[] = [];
  imageSearchTerm = '';
  uploadError: string | null = null;
  maxFileSize = 2 * 1024 * 1024; // 2MB

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private categoryService: CategoryService,
    private imageService: ImageService,
    private route: ActivatedRoute,
    private router: Router,
    private keycloakService: KeycloakService,
    private sanitizer: DomSanitizer
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
      if (product.imageUrl) {
        this.selectedImageUrl = product.imageUrl;
        this.productForm.patchValue({ imageUrl: product.imageUrl });
      }
    });
  }

  loadExistingImages(): void {
    this.imageService.getExistingImages().subscribe({
      next: (images) => {
        this.existingImages = images;
        this.filteredImages = [...images];
      },
      error: (err) => {
        console.error('Failed to load existing images', err);
      }
    });
  }

  filterImages(): void {
    if (!this.imageSearchTerm) {
      this.filteredImages = [...this.existingImages];
      return;
    }
    
    const searchTerm = this.imageSearchTerm.toLowerCase();
    this.filteredImages = this.existingImages
      .filter(img => img.toLowerCase().includes(searchTerm))
      .slice(0, 12);
  }

 // In your ProductFormComponent
onFileSelected(event: any): void {
  this.uploadError = null;
  const file = event.target.files[0];
  
  if (!file) {
    return;
  }

  // Validate file type
  const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
  if (!validTypes.includes(file.type)) {
    this.uploadError = 'Only JPG/PNG images are allowed';
    return;
  }

  // Validate file size (2MB max)
  if (file.size > this.maxFileSize) {
    this.uploadError = 'Image size must be less than 2MB';
    return;
  }

  this.selectedImageFile = file;
  
  // Create preview
  const reader = new FileReader();
  reader.onload = () => {
    this.selectedImageUrl = this.sanitizer.bypassSecurityTrustUrl(reader.result as string);
    this.productForm.patchValue({ imageUrl: file.name });
  };
  reader.readAsDataURL(file);
}

  selectExistingImage(imageUrl: string): void {
    this.selectedImageUrl = imageUrl;
    this.selectedImageFile = null;
    this.productForm.patchValue({ imageUrl: imageUrl });
  }

  clearImageSelection(): void {
    this.selectedImageUrl = null;
    this.selectedImageFile = null;
    this.productForm.patchValue({ imageUrl: '' });
    this.uploadError = null;
  }

  submitForm(): void {
    if (this.productForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    
    if (this.selectedImageFile) {
      this.uploadImageAndSubmit();
    } else {
      this.saveProduct();
    }
  }

  uploadImageAndSubmit(): void {
    if (!this.selectedImageFile) {
      this.saveProduct();
      return;
    }

    this.imageService.uploadImage(this.selectedImageFile).pipe(
      finalize(() => this.isSubmitting = false)
    ).subscribe({
      next: (response) => {
        this.productForm.patchValue({ imageUrl: response.url });
        this.saveProduct();
      },
      error: (err) => {
        console.error('Image upload failed', err);
        this.uploadError = 'Failed to upload image. Please try again.';
      }
    });
  }

  saveProduct(): void {
    const productData = this.productForm.value;

    if (this.isEditMode) {
      this.productService.updateProduct(productData.id, productData).subscribe({
        next: () => this.router.navigate(['/products']),
        error: (err) => {
          console.error('Update failed', err);
          this.isSubmitting = false;
        }
      });
    } else {
      this.productService.addProduct(productData).subscribe({
        next: () => this.router.navigate(['/products']),
        error: (err) => {
          console.error('Create failed', err);
          this.isSubmitting = false;
        }
      });
    }
  }
}