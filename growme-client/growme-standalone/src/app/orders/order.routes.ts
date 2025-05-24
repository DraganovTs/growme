import { Routes } from '@angular/router';

export const ORDER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./order-list/order-list.component').then(m => m.OrderListComponent)
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./order-details/order-details.component').then(m => m.OrderDetailsComponent)
  }
];