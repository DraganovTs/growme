import { Routes } from '@angular/router';
import { ProductFormComponent } from './product-form/product-form.component';

export const PRODUCT_ROUTES: Routes = [
    { 
        path: 'add', 
        component: ProductFormComponent 
    },
    { 
        path: 'edit/:id', 
        component: ProductFormComponent 
    }
];