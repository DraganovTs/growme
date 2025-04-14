export class ShopParams {
    ownerId: string = '';
    categoryId: string = '';
    sort: string = 'name';
    pageIndex: number = 1;
    pageSize: number = 0;
    search?: string;
  }