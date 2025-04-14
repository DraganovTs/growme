import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICategory, IOwner, IProduct } from '../shared/model/product';
import { environment } from '../environment/environments';
import { IPagination } from '../shared/model/pagination';
import { ShopParams } from '../shared/model/shopparams';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ShopService {

  shopParams = new ShopParams();
pagination?: IPagination<IProduct[]>;

  constructor(private  http: HttpClient) { }
  
  getProducts() :Observable<IPagination<IProduct[]>>{
    let params = new HttpParams();
    console.log("page size:");
    console.log(this.shopParams.pageSize);
    if(this.shopParams.ownerId != '') params = params.append('ownerId', this.shopParams.ownerId);
    if(this.shopParams.categoryId != '') params = params.append('categoryId', this.shopParams.categoryId);
    params = params.append('sort', this.shopParams.sort);
    
    params = params.append('pageIndex', this.shopParams.pageIndex);
   if(this.shopParams?.pageSize === 0)  this.shopParams.pageSize=environment.pageSize;
    params = params.append('pageSize', environment.pageSize);
    if(this.shopParams.search) params = params.append('search',this.shopParams.search.trim());

    return this.http.get<IPagination<IProduct[]>>(environment.apiUrl +'products', {params}).pipe(
    
      map(response => {
         this.pagination= response;
         console.log(response);
          return response;
        
      })

    );
  }
  getCategories() {
    return this.http.get<ICategory[]>(environment.apiUrl +'categories');
  }
  getOwners() {
    return this.http.get<IOwner[]>(environment.apiUrl +'owners');
  }

  setShopParams(params: ShopParams) {
    this.shopParams = params;
  }
  getShopParams() {
    return this.shopParams;
  }
}
