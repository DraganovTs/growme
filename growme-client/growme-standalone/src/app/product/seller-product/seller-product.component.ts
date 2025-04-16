import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product-service';
import { KeycloakService } from '../../services/keycloak.service';
import { IProduct } from '../../shared/model/product';

@Component({
  selector: 'app-seller-product',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seller-product.component.html',
  styleUrls: ['./seller-product.component.scss']
})
export class SellerProductComponent implements OnInit {
  products: IProduct[] = []; 
  isLoading = false;
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private keycloakService: KeycloakService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadProducts();
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

    this.productService.getProductsBySeller(userId).subscribe({
      next: (data: IProduct[]) => {
        this.products = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching products:', err);
        this.errorMessage = 'Failed to load products. Please try again.';
        this.isLoading = false;
      }
    });
  }

  editProduct(productId: string): void {
    this.router.navigate(['/products/edit', productId]);
  }

  deleteProduct(productId: string): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.isLoading = true;
      this.productService.deleteProduct(productId).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.productId !== productId);
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error deleting product:', err);
          this.errorMessage = 'Failed to delete product';
          this.isLoading = false;
        }
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
}