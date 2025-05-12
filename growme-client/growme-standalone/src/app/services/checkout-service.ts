import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IDeliveryMethod } from '../shared/model/delivery';
import { map } from 'rxjs/operators';
import { environment } from '../environment/environments';
import { IOrder, IOrderToCreate } from '../shared/model/order';

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {
  baseUrl = environment.apiUrl;
  constructor(private httpClient: HttpClient){

  }

  createOrder(order: IOrderToCreate){
    console.log(order);
    return this.httpClient.post<IOrder>(this.baseUrl + 'order', order)
  }

  getDeliveryMethods() {

    return this.httpClient.get<IDeliveryMethod[]>(
      this.baseUrl + 'deliveryMethods').pipe(
        map(dm => {
          console.log(dm);
          return dm.sort((a, b) => b.price - a.price)
        })
      )
   }
}
