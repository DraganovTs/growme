import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProduct } from '../shared/model/product';
import { environment } from '../environment/environments';
import { Pagination } from '../shared/model/pagination';

@Injectable({
  providedIn: 'root'
})
export class ShopService {

  constructor(private http: HttpClient) { }

  getProducts(){
    return this.http.get<Pagination<IProduct[]>>(environment.apiUrl + "products")
  }
}
