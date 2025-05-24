import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../environment/environments";
import { IOrder } from "../shared/model/order";


@Injectable({ providedIn: 'root' })
export class OrderService {
    baseUrl = environment.orderApi;

    constructor(private http: HttpClient) {}

    getUserOrders(){
        return this.http.get<IOrder[]>(`${this.baseUrl}/orders`);
    }

    getOrderById(orderId: string) {
        return this.http.get<IOrder>(`${this.baseUrl}/orders/${orderId}`);
    }   
}