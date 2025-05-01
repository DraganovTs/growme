import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ImageService } from 'src/app/services/image-service';
import { ICartItem } from 'src/app/shared/model/cart';

@Component({
  selector: 'app-cart-details',
  standalone: true, 
  imports: [CommonModule],
  templateUrl: './cart-details.component.html',
  styleUrl: './cart-details.component.scss'
})
export class CartDetailsComponent {
  @Input() items: ICartItem[] = []
    @Output() increment: EventEmitter<ICartItem> = new EventEmitter<ICartItem>();
    @Output() decrement: EventEmitter<ICartItem>= new EventEmitter<ICartItem>();
    @Output() remove: EventEmitter<ICartItem>= new EventEmitter<ICartItem>();
    @Input() isCart: boolean = true;
    showDetails = false;
    selectedItem: ICartItem | null = null
    
    constructor(private imageService: ImageService) {}

    getImageUrl(imageUrl: string | undefined): string {
      if (!imageUrl) {
        return this.imageService.getDefaultImageUrl();
      }
      return this.imageService.getImageUrl(imageUrl);
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
      this.decrement.emit(item);
   }
   removeCartItem(item: ICartItem) {
      this.remove.emit(item);
   }
}
