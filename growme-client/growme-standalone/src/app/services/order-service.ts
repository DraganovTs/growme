import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../environment/environments";
import { IOrder } from "../shared/model/order";
import { KeycloakService } from "./keycloak.service";


@Injectable({ providedIn: 'root' })
export class OrderService {
    baseUrl = environment.orderApi;

    constructor(private http: HttpClient , private keycloakService: KeycloakService) {}

    getUserOrders(){
        const email = this.keycloakService.getEmail();
        return this.http.get<IOrder[]>(`${this.baseUrl}orders/user/${email}`);
    }

    getOrderById(orderId: string) {
        return this.http.get<IOrder>(`${this.baseUrl}orders/${orderId}`);
    }   
}