import { Routes, RouterModule } from "@angular/router";
import { ProductFormComponent } from "./product-form/product-form.component";
import { ProductListComponent } from "./product-list/product-list.component";


export const PRODUCT_ROUTES: Routes = [
    { path: '', component: ProductListComponent }, 
    { path: 'add', component: ProductFormComponent, data: { isEditMode: false } }, 
    { path: 'edit/:id', component: ProductFormComponent, data: { isEditMode: true } } 
];

