export interface IBid {
  bidId: string;
  price: number;
  message: string;
  status: BidStatus;
  userId?: string;
  userName?: string;
  taskId?: string;
  taskTitle?: string,
  deliveryMethod?: 'PICKUP' | 'DELIVERY' | 'MARKET';
  deliveryIncluded?: boolean;
  proposedHarvestDate?: string ;
  counterOfferPrice?: number;
  counterOfferMessage?: string;
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

export type BidStatus = 
  | 'PENDING'       
  | 'ACCEPTED'      
  | 'REJECTED'      
  | 'COUNTER_OFFER'  
  | 'WITHDRAWN'
  | 'EXPIRED';  