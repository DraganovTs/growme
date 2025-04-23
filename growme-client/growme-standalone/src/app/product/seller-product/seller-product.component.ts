import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';
import { ICategory, IProduct } from '../../shared/model/product';
import { SellerParams } from '../../shared/model/sellerparams';
import { CategoryService } from '../../services/category-service';
import { ImageService } from '../../services/image-service';
import { SellerProductItemComponent } from './seller-product-item/seller-product-item.component';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { FormsModule } from '@angular/forms';
import { SellerService } from '../../services/seller-service';

@Component({
  selector: 'app-seller-product',
  standalone: true,
  imports: [
    CommonModule,
    PaginationModule,
    SellerProductItemComponent,
    FormsModule,
  ],
  templateUrl: './seller-product.component.html',
  styleUrls: ['./seller-product.component.scss']
})
export class SellerProductComponent implements OnInit {
  @ViewChild('searchInput') searchInput?: ElementRef;
  
  // Data
  products: IProduct[] = [];
  categories: ICategory[] = [];
  
  // State
  isLoading = false;
  errorMessage = '';
  totalCount = 0;
  searchQuery = '';
  
  // Configuration
  sortOptions = [
    { name: 'Alphabetical', value: 'name' },
    { name: 'Price: Low to high', value: 'priceAsc' },
    { name: 'Price: High to low', value: 'priceDesc' }
  ];

  constructor(
    private sellerService: SellerService,
    private keycloakService: KeycloakService,
    private categoryService: CategoryService,
    private imageService: ImageService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initializeSellerProducts();
    this.loadCategories();
  }

  private initializeSellerProducts(): void {
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      this.errorMessage = 'User not authenticated';
      return;
    }

    const params = this.sellerService.getSellerParams();
    params.ownerId = userId;
    this.sellerService.setSellerParams(params);
    this.loadProducts();
  }

  private loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.sellerService.getProducts().subscribe({
      next: (response) => {
        this.products = response.dataList || [];
        this.totalCount = response.totalCount || 0;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.errorMessage = 'Failed to load products';
        this.isLoading = false;
      }
    });
  }

  private loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = [{ categoryId: '', categoryName: 'All' }, ...categories];
      },
      error: (err) => {
        console.error('Error loading categories:', err);
      }
    });
  }

  onCategorySelected(categoryId: string): void {
    const params = this.sellerService.getSellerParams();
    params.categoryId = categoryId || undefined;
    params.pageIndex = 1;
    this.sellerService.setSellerParams(params);
    this.loadProducts();
  }

  onSortChange(): void {
    const params = this.sellerService.getSellerParams();
    params.pageIndex = 1;
    this.sellerService.setSellerParams(params);
    this.loadProducts();
  }

  onPageChanged(page: number): void {
    const params = this.sellerService.getSellerParams();
    if (params.pageIndex !== page) {
      params.pageIndex = page;
      this.sellerService.setSellerParams(params);
      this.loadProducts();
    }
  }

  onSearch(): void {
    const params = this.sellerService.getSellerParams();
    params.search = this.searchQuery.trim();
    params.pageIndex = 1;
    this.sellerService.setSellerParams(params);
    this.loadProducts();
  }

  onReset(): void {
    this.searchQuery = '';
    const params = this.sellerService.resetSellerParams();
    const userId = this.keycloakService.getUserId();
    if (userId) {
      params.ownerId = userId;
    }
    this.sellerService.setSellerParams(params);
    this.loadProducts();
  }

  editProduct(productId: string): void {
    this.router.navigate(['/seller/products/edit', productId]);
  }

  deleteProduct(productId: string): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.sellerService.deleteProduct(productId).subscribe({
        next: () => {
          this.loadProducts();
        },
        error: (err) => {
          console.error('Error deleting product:', err);
        }
      });
    }
  }

  addNewProduct(): void {
    this.router.navigate(['/seller/products/add']);
  }

  get currentParams(): SellerParams {
    return this.sellerService.getSellerParams();
  }

  get totalPages(): number {
    return Math.ceil(this.totalCount / this.currentParams.pageSize);
  }
}