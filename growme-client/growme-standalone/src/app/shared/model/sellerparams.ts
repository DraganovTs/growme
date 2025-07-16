export class ISellerParams {
  categoryId?: string;
  sort = 'name';
  pageIndex = 1;
  pageSize = 6;
  search?: string;
  ownerId?: string;
}