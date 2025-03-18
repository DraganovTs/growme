import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AuthCallbackComponent } from './auth/auth-callback/auth-callback.component';

export const routes: Routes = [
    {path: '' , redirectTo: 'welcome', pathMatch: 'full'},
    {path: 'welcome', component: HomeComponent},

    {path: 'shop'
        ,loadChildren: () => import('./shop/shop.routes').then(r => r.SHOP_ROUTES)},
        {
            path: 'products',
            loadChildren: () => import('./product/product.routes').then(r => r.PRODUCT_ROUTES)
        },
        { path: 'auth/callback', component: AuthCallbackComponent },
        { path: '**', redirectTo: '' },
    
];
