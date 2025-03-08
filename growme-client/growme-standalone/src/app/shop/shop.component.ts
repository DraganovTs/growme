import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ShopService } from './shop.service';
import { ICategory, IOwner, IProduct } from '../shared/model/product';
import { CommonModule } from '@angular/common';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { ShopParams } from '../shared/model/shopparams';
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
  shopParams: ShopParams;
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
    //  this.getCategories();
    //  this.getBrands();
  }

   getProducts() {
    this.shopService.getProducts().subscribe({
        next: response => {
           
            this.products = response?.dataList 
            this.shopParams.pageIndex = response?.pageIndex;
            this.shopParams.pageSize = response?.pageSize;
            this.totalCount= response?.count;
          
            

        } ,
        error: error => console.log(error)
     });
   }


  getCategories() {
    // this.shopService.getCategories().subscribe({
    //    next: response => {
    //     console.log(response);
    //     this.categories = [{categoryId:'', categoryName: 'All'}, ...response]
    //    },
    //    error:error =>console.log(error)
    // })
  }
  getOwners() {
    // this.shopService.getOwners().subscribe({
    //    next: response => {
    //     console.log(response);
    //     this.owners = [{ownerId:'', ownerName: 'All'}, ...response]
    //    },
    //    error:error =>console.log(error)
    // })
  }
  onBrandSelected(brandId: number) {
      // const params = this.shopService.getShopParams();
      // params.ownerId = brandId;
      // console.log(params.brandId);
      // params.pageIndex= 1;
      // //if(params.pageSize ==0) params.pageSize=6;
      // this.shopService.setShopParams(params);
      // this.shopParams =params;
      // this.getProducts();
  }
  onCategorySelected(categoryId: number)  {
    // const params = this.shopService.getShopParams();
    //   params.categoryId = categoryId;
    //   params.pageIndex=1;
    //   //if(params.pageSize == 0) params.pageSize=6;
    //   this.shopService.setShopParams(params);
    //   this.shopParams =params;
    //   this.getProducts();
  }
  onSortSelected(event: any) {

    // const params =this.shopService.getShopParams();
    // params.sort = event.target.value;
    // this.shopService.setShopParams(params);
    // this.shopParams = params;
    // this.getProducts();
  }

  onPageChanged(event: any) {
    // const params = this.shopService.getShopParams();
    // if(params.pageIndex !== event.page) {
    //   params.pageIndex = event.page;
    //   this.shopService.setShopParams(params);
    //   this.shopParams = params;
    //   this.getProducts();
    // }
  }
  onSearch(){
    // const params = this.shopService.getShopParams();

    // params.search = this.searchTerm?.nativeElement.value;
    // console.log(params.search);
    // params.pageIndex=1;
    // this.shopService.setShopParams(params);
    // this.shopParams = params;
    // this.getProducts();

  }

  onReset() {
    // if(this.searchTerm) this.searchTerm.nativeElement.value='';
    // this.shopParams = new ShopParams();
    // this.shopService.setShopParams(this.shopParams);
    // this.getProducts();
  }
}