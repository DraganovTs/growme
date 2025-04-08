import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { IProduct } from '../../shared/model/product';
import { ProductService } from '../../services/product-service'
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../services/category-service';
import { ImageDisplay, ImageService } from '../../services/image-service';
import { KeycloakService } from '../../services/keycloak.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { finalize } from 'rxjs';
import { FilenamePipe } from './Filename.Pipe';
import { ProductCreateDTO, ProductUpdateDTO } from '../../shared/model/product-create';


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
  recentImages: ImageDisplay[] = [];


  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private categoryService: CategoryService,
    public imageService: ImageService,
    private route: ActivatedRoute,
    private router: Router,
    private keycloakService: KeycloakService,
    private sanitizer: DomSanitizer,
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadCategories();
    this.loadExistingImages();
    this.loadRecentImages();


    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.isEditMode = true;
      this.loadProduct(productId);
    }
  }

  initializeForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      brand: [''],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: [0, [Validators.required, Validators.min(0.1)]],
      unitsInStock: [0, [Validators.required, Validators.min(1)]],
      imageUrl: [''],
      categoryName: ['', Validators.required],
      ownerName: [{ value: this.keycloakService.getUsername(), disabled: true }, Validators.required], 
    });
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        if (this.isEditMode && this.productForm.value.categoryName) {
          const categoryExists = this.categories.some(
            c => c.categoryName === this.productForm.value.categoryName
          );
          if (!categoryExists) {
            this.productForm.patchValue({ categoryName: '' });
          }
        }
      },
      error: (err) => {
        console.error('Failed to load categories', err);
        this.uploadError = 'Failed to load categories. Please try again.';
      }
    });
  }

  loadProduct(productId: string): void {
    this.productService.getProductById(productId).subscribe((product) => {
      this.productForm.patchValue({
        name: product.name,
        brand: product.brand,
        description: product.description,
        price: product.price,
        unitsInStock: product.unitsInStock,
        imageUrl: product.imageUrl,
        categoryName: product.categoryName, 
        ownerName: product.ownerName
      });
      if (product.imageUrl) {
        this.selectedImageUrl = product.imageUrl;
      }
    });
  }

  loadExistingImages(): void {
    this.imageService.getExistingImages().subscribe({
      next: (images: string[]) => {
        this.existingImages = images;
        this.filteredImages = [...images];
      },
      error: (err) => {
        console.error('Failed to load existing images', err);
        this.uploadError = 'Failed to load existing images. Please try again later.';
      }
    });
  }

  loadRecentImages(): void {
    this.imageService.getRecentImages().subscribe({
      next: (images) => {
        this.recentImages = images;
      },
      error: (err) => {
        console.error('Failed to load recent images', err);
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


 onFileSelected(event: any): void {
  this.uploadError = null;
  const file = event.target.files[0];
  
  if (!file) return;

  // Validate file type and size
  if (!['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)) {
    this.uploadError = 'Only JPG/PNG images are allowed';
    return;
  }

  if (file.size > this.maxFileSize) {
    this.uploadError = 'Image size must be less than 2MB';
    return;
  }

  this.selectedImageFile = file;
  
  // Create preview
  const reader = new FileReader();
  reader.onload = () => {
    this.selectedImageUrl = reader.result as string;
    this.productForm.patchValue({ 
      imageUrl: file.name // Send original filename to backend
    });
  };
  reader.readAsDataURL(file);
}



selectExistingImage(image: ImageDisplay): void {
  this.selectedImageUrl = this.imageService.getImageUrl(image.filename);
  this.selectedImageFile = null;
  this.productForm.patchValue({ imageUrl: image.filename });
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
  
    this.isSubmitting = true;
    
    this.imageService.uploadImage(this.selectedImageFile).pipe(
      finalize(() => this.isSubmitting = false)
    ).subscribe({
      next: (response: any) => {  // Use proper interface instead of 'any' in production
        try {
          // Handle both string and object responses
          const imageUrl = typeof response === 'string' 
            ? response 
            : response?.url || response?.filename || this.selectedImageFile?.name;
          
          if (!imageUrl) {
            throw new Error('No image URL received');
          }
  
          // Extract just the filename if it's a full URL
          const filename = imageUrl.split('/').pop() || imageUrl;
          this.productForm.patchValue({ imageUrl: filename });
          this.saveProduct();
        } catch (err) {
          console.error('Error processing image upload:', err);
          this.uploadError = 'Failed to process image upload response';
        }
      },
      error: (err) => {
        console.error('Image upload failed', err);
        this.uploadError = 'Failed to upload image. Please try again.';
      }
    });
  }

  saveProduct(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }
  
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      this.uploadError = 'You must be logged in to create/update a product';
      return;
    }
  
    const formValue = this.productForm.value;
    
    // Enhanced category validation
    if (!formValue.categoryName || formValue.categoryName === 'undefined') {
      this.uploadError = 'Please select a valid category';
      return;
    }
  
    // Proper image URL handling
    let finalImageUrl: string | null = null;
    if (this.selectedImageUrl) {
      if (typeof this.selectedImageUrl === 'string') {
        // If it's already a string (existing image name)
        finalImageUrl = this.imageService.getImageUrl(this.selectedImageUrl);
      } else {
        // If it's a SafeValue (new uploaded image)
        const unsafeUrl = this.selectedImageUrl.toString();
        // Extract just the filename if needed
        finalImageUrl = unsafeUrl; // or parse filename if needed
      }
    }
  
    const productData = {
      ...formValue,
      price: Number(formValue.price),
      unitsInStock: Number(formValue.unitsInStock),
      imageUrl: finalImageUrl,
      ownerId: userId
    };
  
    if (this.isEditMode) {
      const productId = this.route.snapshot.paramMap.get('id')!;
      this.productService.updateProduct(productId, {
        ...productData,
        productId: productId
      }).subscribe({
        next: () => this.router.navigate(['/products']),
        error: (err) => this.handleError(err)
      });
    } else {
      this.productService.addProduct(productData).subscribe({
        next: () => this.router.navigate(['/products']),
        error: (err) => this.handleError(err)
      });
    }
  }
  private handleError(err: any): void {
    console.error('Operation failed', err);
    this.isSubmitting = false;
    
    if (err.status === 400) {
      if (err.error?.message?.includes('category')) {
        this.uploadError = 'Invalid category selected. Please choose a valid category.';
      } else if (err.error?.errors) {
        // Handle validation errors from backend
        const errorMessages = Object.values(err.error.errors).join(', ');
        this.uploadError = `Validation errors: ${errorMessages}`;
      } else {
        this.uploadError = 'Invalid form data. Please check all fields.';
      }
    } else {
      this.uploadError = err.message || 'Operation failed. Please try again.';
    }
  }

  public getImageUrlForExisting(image: string): string {
    return this.imageService.getImageUrl(image);
  }
  
  getImageUrlForPreview(image: string | SafeUrl): string | SafeUrl {
    if (typeof image === 'string') {
      // Check if it's already a full URL or needs prefix
      return image.startsWith('http') ? image : this.imageService.getImageUrl(image);
    }
    return image; 
  }
  }