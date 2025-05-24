import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AuthCallbackComponent } from './auth/auth-callback/auth-callback.component';
import { CompleteProfileComponent } from './core/complete-profile/complete-profile.component';
import { SellerProductComponent } from './product/seller-product/seller-product.component';

export const routes: Routes = [
    { path: '', redirectTo: 'welcome', pathMatch: 'full' },
    { path: 'welcome', component: HomeComponent },
    { path: 'complete-profile', component: CompleteProfileComponent },
    { path: 'auth/callback', component: AuthCallbackComponent },
    
    { 
        path: 'shop',
        loadChildren: () => import('./shop/shop.routes').then(r => r.SHOP_ROUTES)
    },
    {
        path: 'products',
        loadChildren: () => import('./product/product.routes').then(r => r.PRODUCT_ROUTES)
    },
    { 
        path: 'seller/products', 
        component: SellerProductComponent 
    },
    {
        path: 'about',
        loadComponent: () => import('./core/about/about.component').then(m => m.AboutComponent)
      },
    {
        path: 'cart',
        loadComponent: () => import('./cart/cart.component').then(m => m.CartComponent)
      },
      {
          path: 'checkout',
          loadComponent: () => import('./check-out/check-out.component').then(m => m.CheckoutComponent)
        },
        {
         path: 'orders',
         loadChildren: () => import('./orders/order.routes').then(m => m.ORDER_ROUTES)
        },
      
    
    { path: '**', redirectTo: 'welcome' }
];