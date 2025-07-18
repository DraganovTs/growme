import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ShopService } from '../services/shop.service';
import { ICategory, IOwner, IProduct } from '../shared/model/product';
import { CommonModule } from '@angular/common';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { IShopParams } from '../shared/model/shopparams';
import { ProductItemComponent } from "./product-item/product-item.component";

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, PaginationModule, ProductItemComponent],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss',
})
export class ShopComponent implements OnInit {
  @ViewChild('search') searchTerm?: ElementRef;
  products: IProduct[] = [];
  categories: ICategory[] = [];
  owners: IOwner[] = [];
  shopParams: IShopParams;
  totalCount=0;
  sortOptions = [
    {name: 'Alphabetical', value: 'name'},
    {name: 'Price: Low to high', value:'priceAsc'},
    {name: 'Price: High to low', value: 'priceDesc'}

  ];
  constructor(private shopService: ShopService){
    this.shopParams = this.shopService.getShopParams();

  }

  ngOnInit(): void {
     this.getProducts();
     this.getCategories();
     this.getOwners();
  }

  getProducts() {
    this.shopService.getProducts().subscribe({
      next: response => {
        console.log('Products API Response:', response);
        this.products = response?.dataList || [];
        this.totalCount = response?.totalCount || 0;
        this.shopParams.pageIndex = response?.pageIndex || 1;
        this.shopParams.pageSize = response?.pageSize || 6;
      },
      error: error => {
        console.log(error);
        this.products = [];
        this.totalCount = 0;
      }
    });
  }


  getCategories() {
    this.shopService.getCategories().subscribe({
       next: response => {
        console.log(response);
        this.categories = [{categoryId:'', categoryName: 'All'}, ...response]
       },
       error:error =>console.log(error)
    })
  }

  getOwners() {
    console.log('Starting to fetch owners...');
    this.shopService.getOwners().subscribe({
      next: (response: any) => {
        console.log('Raw owners API response:', response);
        console.log('Is array:', Array.isArray(response));
        console.log('Response type:', typeof response);

        const ownersArray = Array.isArray(response) ? response : [];
        console.log('Processed owners array:', ownersArray);

        this.owners = [{ownerId: '', ownerName: 'All'}, ...ownersArray];
        console.log('Final owners list:', this.owners);
      },
      error: error => {
        console.error('Error fetching owners:', error);
        this.owners = [{ownerId: '', ownerName: 'All'}];
      }
    });
  }

  onBrandSelected(ownerId: string) {
    const params = this.shopService.getShopParams();
    params.ownerId = ownerId === '' ? undefined : ownerId;
    params.pageIndex = 1;
    this.shopService.setShopParams(params);
    this.shopParams = params;
    this.getProducts();
  }

  onCategorySelected(categoryId: string)  {
    const params = this.shopService.getShopParams();
    params.categoryId = categoryId;
      params.pageIndex=1;
      //if(params.pageSize == 0) params.pageSize=6;
      this.shopService.setShopParams(params);
      this.shopParams =params;
      this.getProducts();
  }
  onSortSelected(event: any) {

    const params =this.shopService.getShopParams();
    params.sort = event.target.value;
    this.shopService.setShopParams(params);
    this.shopParams = params;
    this.getProducts();
  }

  onPageChanged(event: any) {
    const params = this.shopService.getShopParams();
    if(params.pageIndex !== event.page) {
      params.pageIndex = event.page;
      this.shopService.setShopParams(params);
      this.shopParams = params;
      this.getProducts();
    }
  }
  onSearch(){
    const params = this.shopService.getShopParams();

    params.search = this.searchTerm?.nativeElement.value;
    console.log(params.search);
    params.pageIndex=1;
    this.shopService.setShopParams(params);
    this.shopParams = params;
    this.getProducts();

  }

  onReset() {
    if(this.searchTerm) this.searchTerm.nativeElement.value='';
    this.shopParams = new IShopParams();
    this.shopService.setShopParams(this.shopParams);
    this.getProducts();
  }
}
