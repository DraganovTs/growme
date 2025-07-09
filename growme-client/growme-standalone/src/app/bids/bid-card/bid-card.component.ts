import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Bid } from 'src/app/shared/model/bid';

@Component({
  standalone: true,
  selector: 'app-bid-card',
  imports: [CommonModule, RouterModule],
  templateUrl: './bid-card.component.html',
  styleUrl: './bid-card.component.scss'
})
export class BidCardComponent {
  @Input() bid!: Bid;
  @Input() isTaskOwner = false;
  @Input() taskStatus = '';
  
  @Output() acceptBid = new EventEmitter<string>();
  @Output() rejectBid = new EventEmitter<string>();
}
