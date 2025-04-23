import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product-service';
import { KeycloakService } from '../../services/keycloak.service';
import { ICategory, IProduct } from '../../shared/model/product';
import { SellerParams } from '../../shared/model/sellerparams';
import { CategoryService } from '../../services/category-service';
import { ImageService } from '../../services/image-service';

@Component({
  selector: 'app-seller-product',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seller-product.component.html',
  styleUrls: ['./seller-product.component.scss']
})
export class SellerProductComponent implements OnInit {
  @ViewChild('search') searchTerm?: ElementRef;
  products: IProduct[] = [];
  categories: ICategory[] = [];
  sellerParams: SellerParams;
  totalCount = 0;
  isLoading = false;
  errorMessage = '';
  
  sortOptions = [
    {name: 'Alphabetical', value: 'name'},
    {name: 'Price: Low to high', value: 'priceAsc'},
    {name: 'Price: High to low', value: 'priceDesc'}
  ];

  constructor(
    private productService: ProductService,
    private keycloakService: KeycloakService,
    private router: Router,
    private categoryService: CategoryService,
    private imageService: ImageService
  ) {
    this.sellerParams = new SellerParams();
  }

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      this.errorMessage = 'User not authenticated';
      this.isLoading = false;
      return;
    }

    this.sellerParams.ownerId = userId; 

    this.productService.getProductsBySeller(this.sellerParams).subscribe({
      next: (response) => {
        this.products = response.dataList || [];
        this.totalCount = response.totalCount || 0;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching products:', err);
        this.errorMessage = 'Failed to load products';
        this.isLoading = false;
      }
    });
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (response) => {
        this.categories = [{categoryId: '', categoryName: 'All'}, ...response];
      },
      error: (err) => console.error('Error loading categories:', err)
    });
  }

  onCategorySelected(categoryId: string): void {
    this.sellerParams.categoryId = categoryId === '' ? undefined : categoryId;
    this.sellerParams.pageIndex = 1; 
    this.loadProducts(); 
  }

  onSortSelected(event: any): void {
    this.sellerParams.sort = event.target.value;
    this.sellerParams.pageIndex = 1; 
    this.loadProducts(); 
  }

  onPageChanged(event: any): void {
    if (this.sellerParams.pageIndex !== event.page) {
      this.sellerParams.pageIndex = event.page;
      this.loadProducts();
    }
  }

  onSearch(): void {
    this.sellerParams.search = this.searchTerm?.nativeElement.value;
    this.sellerParams.pageIndex = 1;
    this.loadProducts();
  }

  onReset(): void {
    if (this.searchTerm) this.searchTerm.nativeElement.value = '';
    this.sellerParams = new SellerParams();
    this.loadProducts();
  }

  editProduct(productId: string): void {
    this.router.navigate(['/products/edit', productId]);
  }

  deleteProduct(productId: string): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.productService.deleteProduct(productId).subscribe({
        next: () => this.loadProducts(),
        error: (err) => console.error('Error deleting product:', err)
      });
    }
  }

  addNewProduct(): void {
    this.router.navigate(['/products/add']);
  }

  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'assets/images/default-product.png';
  }

  get totalPages(): number {
    return Math.ceil(this.totalCount / this.sellerParams.pageSize);
  }

  getImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) {
      return this.imageService.getDefaultImageUrl();
    }
    return this.imageService.getImageUrl(imageUrl);
  }
}