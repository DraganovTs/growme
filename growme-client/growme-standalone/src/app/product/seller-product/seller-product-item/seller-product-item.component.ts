import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImageService } from '../../../services/image-service';
import { IProduct } from '../../../shared/model/product';

@Component({
  selector: 'app-seller-product-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seller-product-item.component.html',
  styleUrls: ['./seller-product-item.component.scss']
})
export class SellerProductItemComponent {
  @Input() product!: IProduct;
  @Output() edit = new EventEmitter<string>();
  @Output() delete = new EventEmitter<string>(); 

  constructor(private imageService: ImageService) {}

  get imageUrl(): string {
    if (!this.product?.imageUrl) {
      return this.imageService.getDefaultImageUrl();
    }
    return this.imageService.getImageUrl(this.product.imageUrl);
  }

  editProduct(event: Event): void {
    event.stopPropagation();
    this.edit.emit(this.product.productId);
  }

  deleteProduct(event: Event): void {
    this.delete.emit(this.product.productId);

  }
}