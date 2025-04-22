import { Injectable } from '@angular/core';
import { ProductService } from './product-service';
import { ShopParams } from '../shared/model/shopparams';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private shopParams = new ShopParams();

  constructor(private productService: ProductService) {}

  getShopParams() {
    return this.shopParams;
  }

  setShopParams(params: ShopParams) {
    this.shopParams = params;
  }
}