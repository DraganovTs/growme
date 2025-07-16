import { Injectable } from "@angular/core";
import { environment } from "../environment/environments";
import { HttpClient, HttpParams } from "@angular/common/http";
import { IProduct, ProductResponseListDTO } from "../shared/model/product";
import { Observable } from "rxjs";
import { IProductCreateDTO, ProductUpdateDTO } from "../shared/model/product-create";
import { ISellerParams } from "../shared/model/sellerparams";

@Injectable({
    providedIn: 'root'
})

export class ProductService {
 
    private apiUrl = environment.apiUrl + 'products'

    constructor(private http: HttpClient){}

    getProducts(): Observable<IProduct[]> {
        return this.http.get<IProduct[]>(this.apiUrl);
      }

      addProduct(product: IProductCreateDTO): Observable<IProduct> {
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

      getProductsBySeller(params: ISellerParams): Observable<ProductResponseListDTO> {
        const httpParams = new HttpParams()
          .set('categoryId', params.categoryId || '')
          .set('ownerId', params.ownerId || '')
          .set('sort', params.sort)
          .set('pageIndex', params.pageIndex.toString())
          .set('pageSize', params.pageSize.toString())
          .set('search', params.search || '');
      
        return this.http.get<ProductResponseListDTO>(`${this.apiUrl}/seller`, { params: httpParams });
      }
}