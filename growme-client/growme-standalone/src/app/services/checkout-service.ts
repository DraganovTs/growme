import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IDeliveryMethod } from '../shared/model/delivery';
import { map } from 'rxjs/operators';
import { environment } from '../environment/environments';
import { IOrder, IOrderToCreate } from '../shared/model/order';
import { KeycloakService } from './keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {
  baseUrl = environment.orderApi;
  constructor(private httpClient: HttpClient ) { }

  
  createOrder(order: IOrderToCreate){
    console.log(order);
    return this.httpClient.post<IOrder>(this.baseUrl + 'orders', order )
  }

  getDeliveryMethods() {

    return this.httpClient.get<IDeliveryMethod[]>(
      this.baseUrl +'deliverymethods').pipe(
        map(dm => {
          console.log(dm);
          return dm.sort((a, b) => b.price - a.price)
        })
      )
   }
}
