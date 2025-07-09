import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-bid-list',
  standalone: true,
  imports: [CommonModule , RouterModule],
  templateUrl: './bid-list.component.html',
  styleUrl: './bid-list.component.scss'
})
export class BidListComponent {

}
