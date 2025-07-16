import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BidCardComponent } from '../bid-card/bid-card.component';
import { IBid } from 'src/app/shared/model/bid';

@Component({
  selector: 'app-bid-list',
  standalone: true,
  imports: [CommonModule ,BidCardComponent],
  templateUrl: './bid-list.component.html',
  styleUrl: './bid-list.component.scss'
})
export class BidListComponent {
  @Input() bids: IBid[] = [];
  @Input() isTaskOwner = false;
  @Input() taskStatus = '';
  @Output() acceptBid = new EventEmitter<string>();
  @Output() rejectBid = new EventEmitter<string>();

  trackById(index: number, bid: IBid): string {
    return bid.id;
  }
}
