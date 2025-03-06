import { Component, OnInit } from '@angular/core';
import { ShopService } from './shop.service';
import { IProduct } from '../shared/model/product';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss',
})
export class ShopComponent implements OnInit {

constructor(private shopService: ShopService){}
  products: IProduct[] = [];
  ngOnInit(): void {
    this.shopService.getProducts().subscribe({
      next: response => this.products = response.dataList,
      error: error => console.log(error)
    })
  }

}
