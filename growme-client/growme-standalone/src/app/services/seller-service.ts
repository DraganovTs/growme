import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { IPagination } from '../shared/model/pagination';
import { IProduct } from '../shared/model/product';
import { ISellerParams } from '../shared/model/sellerparams';
import { environment } from '../environment/environments';
import { catchError, delay, Observable, of, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private baseUrl = environment.apiUrl;
  private sellerParams = new ISellerParams();

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
 
  setSellerParams(params: ISellerParams) {
    this.sellerParams = params;
  }

  getSellerParams() {
    return this.sellerParams;
  }

  resetSellerParams() {
    this.sellerParams = new ISellerParams();
    return this.sellerParams;
  }

  deleteProduct(params: {productId: string, userId: string}): Observable<void> {
    const url = `${this.baseUrl}products/${params.productId}/${params.userId}`;
    return this.http.delete<void>(url).pipe(
      catchError(error => {
        console.error('Delete failed:', error);
        return throwError(() => error);
      })
    );
  }
}