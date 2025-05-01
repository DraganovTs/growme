import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IProduct } from '../../shared/model/product';
import { ImageService } from '../../services/image-service';
import { CartService } from 'src/app/services/cart-service';

@Component({
  selector: 'app-product-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-item.component.html',
  styleUrls: ['./product-item.component.scss']
})
export class ProductItemComponent {
  @Input() product!: IProduct; 
  showDetails = false;
  quantity = 1;
  imageLoaded = false;
  
  constructor(private imageService: ImageService, private cartService: CartService) {}

  get imageUrl(): string {
    if (!this.product?.imageUrl) {
      return this.imageService.getDefaultImageUrl();
    }
    return this.imageService.getImageUrl(this.product.imageUrl);
  }




  openProductDetails(event: Event): void {
    event.stopPropagation();
    this.showDetails = true;
    document.body.style.overflow = 'hidden';
  }

  closeProductDetails(): void {
    this.showDetails = false;
    document.body.style.overflow = '';
  }

  incrementQuantity(): void {
    this.quantity++;
  }

  decrementQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  addItemToCart(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.product&&this.cartService.addItemToCart(this.product);

    this.closeProductDetails();
    this.quantity = 1;
  }

  onImageLoad(): void {
    this.imageLoaded = true;
  }
}
