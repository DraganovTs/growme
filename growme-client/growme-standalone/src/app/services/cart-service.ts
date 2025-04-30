import { Injectable } from "@angular/core";
import { ICart, ICartTotals } from "../shared/model/cart";
import { BehaviorSubject } from "rxjs";
import { environment } from "../environment/environments";
import { HttpClient } from "@angular/common/http";

@Injectable({
    providedIn: 'root'  
})
export class CartService {

    private cartSource = new BehaviorSubject<ICart | null>(null);
    cart$ = this.cartSource.asObservable();
    private cartTotalsSource = new BehaviorSubject<ICartTotals | null>(null);
    cartTotals$ = this.cartTotalsSource.asObservable();
    basketUrl = environment.basketApi;

    constructor(private http: HttpClient){}


    
}