import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ImageService } from 'src/app/services/image-service';
import { ICartItem } from 'src/app/shared/model/cart';

@Component({
  selector: 'app-cart-details',
  standalone: true, 
  imports: [CommonModule],
  templateUrl: './cart-details.component.html',
  styleUrl: './cart-details.component.scss'
})
export class CartDetailsComponent implements OnInit {
  @Input() items: ICartItem[] = []
    @Output() increment: EventEmitter<ICartItem> = new EventEmitter<ICartItem>();
    @Output() decrement: EventEmitter<ICartItem>= new EventEmitter<ICartItem>();
    @Output() remove: EventEmitter<ICartItem>= new EventEmitter<ICartItem>();
    @Input() isCart: boolean = true;
    showDetails = false;
    selectedItem: ICartItem | null = null
    
    constructor(private imageService: ImageService) {}

  ngOnInit(): void {
     console.log('Cart items:', this.items);

  }

  getImageUrl(item: ICartItem): string {
    if (!item?.imageUrl) {
      console.warn('Missing imageUrl for item:', item);
      return this.imageService.getDefaultImageUrl();
    }
    
    // Ensure the URL is properly encoded
    try {
      const url = this.imageService.getImageUrl(item.imageUrl);
      console.log('Generated image URL:', url);
      return url;
    } catch (e) {
      console.error('Error generating image URL:', e);
      return this.imageService.getDefaultImageUrl();
    }
  }

    openProductDetails(item: ICartItem, event: Event): void {
      event.stopPropagation();
      this.selectedItem = item;
      this.showDetails = true;
      document.body.style.overflow = 'hidden';
    }
  
    closeProductDetails(): void {
      this.showDetails = false;
      document.body.style.overflow = '';
    }

   incrementItemQuantity(item: ICartItem) {
      this.increment.emit(item);
   }
   decrementItemQuantity(item: ICartItem) {
    if (item.quantity <= 1) return; 
    this.decrement.emit(item);
  }
   removeCartItem(item: ICartItem) {
      this.remove.emit(item);
   }

   handleImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = this.imageService.getDefaultImageUrl();
  }
}
