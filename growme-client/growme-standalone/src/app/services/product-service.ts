import { Injectable } from "@angular/core";
import { environment } from "../environment/environments";
import { HttpClient } from "@angular/common/http";
import { IProduct } from "../shared/model/product";
import { Observable } from "rxjs";
import { ProductCreateDTO } from "../shared/model/product-create";
import { ProductUpdateDTO } from "../shared/model/product-update";

@Injectable({
    providedIn: 'root'
})

export class ProductService {
    private apiUrl = environment.apiUrl + 'products'

    constructor(private http: HttpClient){}

    getProducts(): Observable<IProduct[]> {
        return this.http.get<IProduct[]>(this.apiUrl);
      }

      addProduct(product: ProductCreateDTO): Observable<IProduct> {
        return this.http.post<IProduct>(this.apiUrl, product);
    }

    updateProduct(id: string, product: ProductUpdateDTO): Observable<IProduct> {
        return this.http.put<IProduct>(`${this.apiUrl}/${id}`, product);
    }

    getProductById(id: string): Observable<IProduct> {
        return this.http.get<IProduct>(`${this.apiUrl}/${id}`);
      }

      deleteProduct(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
      }
}