export class IShopParams {
    ownerId?: string = '';
    categoryId: string = '';
    sort: string = 'name';
    pageIndex: number = 1;
    pageSize: number = 6;
    search?: string;
  }