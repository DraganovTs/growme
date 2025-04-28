import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { IPagination } from '../shared/model/pagination';
import { IProduct } from '../shared/model/product';
import { SellerParams } from '../shared/model/sellerparams';
import { environment } from '../environment/environments';
import { delay, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private baseUrl = environment.apiUrl;
  private sellerParams = new SellerParams();

  constructor(private http: HttpClient) {}

  getProducts() {
    let params = this.buildParams();
    return this.http.get<IPagination<IProduct[]>>(`${this.baseUrl}products/seller`, { params });
  }

  private buildParams(): HttpParams {
    let params = new HttpParams();

    if (this.sellerParams.ownerId) {
      params = params.append('ownerId', this.sellerParams.ownerId);
    }

    if (this.sellerParams.categoryId) {
      params = params.append('categoryId', this.sellerParams.categoryId);
    }

    if (this.sellerParams.search) {
      params = params.append('search', this.sellerParams.search.trim());
    }

    params = params.append('sort', this.sellerParams.sort);
    params = params.append('pageIndex', this.sellerParams.pageIndex.toString());
    params = params.append('pageSize', this.sellerParams.pageSize.toString());

    return params;
  }
 
  setSellerParams(params: SellerParams) {
    this.sellerParams = params;
  }

  getSellerParams() {
    return this.sellerParams;
  }

  resetSellerParams() {
    this.sellerParams = new SellerParams();
    return this.sellerParams;
  }

  deleteProduct(params: {productId: string, userId: string}): Observable<any> {
    console.log(`${this.baseUrl}products/${params.productId}`, {
      params: new HttpParams().set('userId', params.userId)
    })
    return this.http.delete(`${this.baseUrl}products/${params.productId}`, {
      params: new HttpParams().set('userId', params.userId)
    });
  }
}