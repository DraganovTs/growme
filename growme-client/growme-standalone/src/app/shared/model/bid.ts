export interface IBid {
  id: string;
  price: number;
  message: string;
  status: 'PENDING' | 'ACCEPTED' | 'CONFIRMED' | 'IN_PROGRESS' | 'READY' |'SHIPPED'  |'DELIVERED'  |'COMPLETED'  |'CANCELLED' |'REJECTED' |'EXPIRED' | 'COUNTER_OFFER';
  userId?: string;
  userName?: string;
  taskId?: string;
  taskTitle?: string,
  deliveryMethod?: 'PICKUP' | 'DELIVERY' | 'MARKET';
  deliveryIncluded?: boolean;
  proposedHarvestDate?: string ;
  createdAt?: string;
  updatedAt?: string; 
}

export interface BidResponseListDTO {
    dataList: IBid[];
    totalCount: number;
    pageIndex?: number;
    pageSize?: number;
    totalPages?: number;
}