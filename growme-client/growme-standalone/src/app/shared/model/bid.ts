export interface Bid {
  id: string;
  price: number;
  message: string;
  status: 'PENDING' | 'ACCEPTED' | 'IN_PROGRESS' | 'DELIVERED' | 'PAID' |'REJECTED';
  userId?: string;
  growerName?: string;
  taskId?: string;
  deliveryMethod?: 'PICKUP' | 'DELIVERY' | 'MARKET';
  deliveryIncluded?: boolean;
  proposedHarvestDate?: string ;
  createdAt?: string;
  updatedAt?: string; 
}