import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { IProduct } from '../../shared/model/product';
import { ImageService } from '../../services/image-service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-product-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-item.component.html',
  styleUrl: './product-item.component.scss'
})
export class ProductItemComponent implements OnChanges {
  @Input() product?: IProduct;
  currentImageUrl = '';
  showDebug = true; // Set to false in production

  constructor(public imageService: ImageService) {
    this.currentImageUrl = this.imageService.getDefaultImageUrl();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['product']) {
      this.loadImage();
    }
  }

  private loadImage() {
    // Start with default image
    this.currentImageUrl = this.imageService.getDefaultImageUrl();

    if (!this.product?.imageUrl) {
      return;
    }

    // First try the raw URL
    this.tryImageUrl(this.product.imageUrl).then(success => {
      if (!success && this.product?.imageUrl) { // Additional check here
        // If raw URL fails, try cleaned version
        const cleaned = this.cleanImageName(this.product.imageUrl);
        this.tryImageUrl(this.imageService.getImageUrl(cleaned)).then(success => {
          if (!success) {
            this.currentImageUrl = this.imageService.getDefaultImageUrl();
          }
        });
      }
    });
  }

  private cleanImageName(name: string): string {
    // Add null check for name
    if (!name) return '';
    
    return name
      .split('/').pop() || '' // Remove paths
      .replace(/[^a-zA-Z0-9.]/g, ''); // Remove special chars
  }

  private tryImageUrl(url: string): Promise<boolean> {
    return new Promise(resolve => {
      const img = new Image();
      img.onload = () => {
        this.currentImageUrl = url;
        resolve(true);
      };
      img.onerror = () => resolve(false);
      img.src = url;
    });
  }
}