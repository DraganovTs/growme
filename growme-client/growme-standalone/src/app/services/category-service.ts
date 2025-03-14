import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../environment/environments";
import { ICategory } from "../shared/model/product";

@Injectable({
    providedIn: 'root'
})

export class CategoryService{
    constructor(private http: HttpClient){}


    getCategories() {
        return this.http.get<ICategory[]>(environment.apiUrl +'categories');
      }
}