import { Component, OnInit, SecurityContext } from '@angular/core';
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
      ownerName: [{ value: this.keycloakService.getUsername(), disabled: true }, Validators.required]
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
  
    // Validate file
    if (!['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)) {
      this.uploadError = 'Only JPG/PNG images are allowed';
      return;
    }
  
    if (file.size > this.maxFileSize) {
      this.uploadError = 'Image size must be less than 2MB';
      return;
    }
  
    this.selectedImageFile = file;
    
    // 1. Update the form FIRST
    this.productForm.patchValue({ imageUrl: file.name });
  
    // 2. Then set the preview
    this.selectedImageUrl = window.URL.createObjectURL(file);

    console.log('Selected file name:', file.name);

  }



  selectExistingImage(image: ImageDisplay): void {
    // 1. Update the form FIRST
    this.productForm.patchValue({ imageUrl: image.filename });
  
    // 2. Then set the preview
    this.selectedImageUrl = this.imageService.getImageUrl(image.filename);
    this.selectedImageFile = null;

    console.log('Existing image filename:', image.filename);

  }

  clearImageSelection(): void {
    // Clean up any object URL first
    if (this.selectedImageUrl && typeof this.selectedImageUrl === 'string' && this.selectedImageUrl.startsWith('blob:')) {
      window.URL.revokeObjectURL(this.selectedImageUrl);
    }
    
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
    
    this.imageService.uploadImage(this.selectedImageFile).subscribe({
      next: (response) => {
        if (!response.filename) {
          throw new Error('Server did not return filename!');
        }
        
        // Update form IMMEDIATELY
        this.productForm.patchValue({ imageUrl: response.filename });
        this.saveProduct();
      },
      error: (err) => {
        this.isSubmitting = false;
        this.uploadError = err.message;
      }
    });
  }

  saveProduct(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }
  
    // Get RAW form values (including disabled fields)
    const formValue = this.productForm.getRawValue();
  
    const productData = {
      ...formValue,
      price: Number(formValue.price),
      unitsInStock: Number(formValue.unitsInStock),
      ownerId: this.keycloakService.getUserId(),
      imageUrl: formValue.imageUrl || ''  // Fallback to empty string if null
    };
  
    // DEBUG: Log the data before saving
    console.log('Saving product with imageUrl:', productData.imageUrl);
  
    if (this.isEditMode) {
      const productId = this.route.snapshot.paramMap.get('id')!;
      this.productService.updateProduct(productId, productData).subscribe({
        next: () => this.cleanUpAndNavigate(),
        error: (err) => this.handleError(err)
      });
    } else {
      this.productService.addProduct(productData).subscribe({
        next: () => this.cleanUpAndNavigate(),
        error: (err) => this.handleError(err)
      });
    }
    console.log('Form imageUrl before save:', this.productForm.value.imageUrl);

  }
  
  private cleanUpAndNavigate(): void {
    // Clean up any object URLs
    if (this.selectedImageUrl && typeof this.selectedImageUrl === 'string' && this.selectedImageUrl.startsWith('blob:')) {
      window.URL.revokeObjectURL(this.selectedImageUrl);
    }
    console.log('Final imageUrl value:', this.productForm.value.imageUrl); // Debug log
    this.router.navigate(['/products']);
  }

  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = this.imageService.getDefaultImageUrl();
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
    if (!image) return this.imageService.getDefaultImageUrl();
    
    if (typeof image === 'string') {
      // Check if it's already a blob URL (preview)
      if (image.startsWith('blob:')) {
        return image;
      }
      // Check if it's already a full URL or needs prefix
      return image.startsWith('http') ? image : this.imageService.getImageUrl(image);
    }
    return image;
  }

  private getCleanFilename(url: string): string {
    // Decode URI components first
    let cleanName = decodeURIComponent(url);
    // Extract just the filename part
    cleanName = cleanName.split('/').pop() || cleanName;
    // Remove any query parameters if present
    cleanName = cleanName.split('?')[0];
    // Remove any fragments if present
    cleanName = cleanName.split('#')[0];
    return cleanName;
}

  getImageDisplayName(image: ImageDisplay | string | SafeUrl): string {
    if (typeof image === 'string') {
      const cleanName = this.getCleanFilename(image);
      return cleanName.replace(/_/g, ' ').replace(/\.[^/.]+$/, "");
    }
    
    if (image && 'displayName' in image) {
      // Handle ImageDisplay case
      return image.displayName || image.filename;
    }
    
    // Handle SafeUrl case
    const urlStr = this.sanitizer.sanitize(SecurityContext.URL, image as SafeUrl) || '';
    const cleanName = this.getCleanFilename(urlStr);
    return cleanName.replace(/_/g, ' ').replace(/\.[^/.]+$/, "");
}

ngOnDestroy(): void {
  this.clearImageSelection();
}

  }