// product-item.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IProduct } from '../../shared/model/product';
import { ImageService } from '../../services/image-service';

@Component({
  selector: 'app-product-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-item.component.html',
  styleUrls: ['./product-item.component.scss']
})
export class ProductItemComponent {
  @Input() product!: IProduct; 
  
  constructor(private imageService: ImageService) {}

  get imageUrl(): string {
    if (!this.product?.imageUrl) {
      return this.imageService.getDefaultImageUrl();
    }
    return this.imageService.getImageUrl(this.product.imageUrl);
  }
}