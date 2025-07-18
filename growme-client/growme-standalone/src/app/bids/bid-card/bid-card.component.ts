import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BidStatus, IBid } from 'src/app/shared/model/bid';

@Component({
  standalone: true,
  selector: 'app-bid-card',
  imports: [CommonModule, RouterModule],
  templateUrl: './bid-card.component.html',
  styleUrl: './bid-card.component.scss'
})
export class BidCardComponent {
  @Input() bid!: IBid;
  @Input() isTaskOwner = false;
  @Input() taskStatus = '';
  
 @Output() acceptBid = new EventEmitter<string>();
  @Output() rejectBid = new EventEmitter<string>();
  @Output() withdrawBid = new EventEmitter<string>();
  @Output() counterOffer = new EventEmitter<IBid>();
  @Output() acceptCounter = new EventEmitter<string>();


  getStatusIcon(status: BidStatus): string {
    switch(status) {
      case 'ACCEPTED': return 'fa-check-circle';
      case 'REJECTED': return 'fa-times-circle';
      case 'COUNTER_OFFER': return 'fa-exchange-alt';
      case 'WITHDRAWN': return 'fa-trash-alt';
      case 'EXPIRED': return 'fa-clock';
      default: return 'fa-hourglass-half';
    }
  }
}
