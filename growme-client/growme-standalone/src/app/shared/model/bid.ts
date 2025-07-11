export interface Bid {
  id: string;
  price: number;
  message: string;
  status: 'PENDING' | 'ACCEPTED' | 'CONFIRMED' | 'IN_PROGRESS' | 'READY' |'SHIPPED'  |'DELIVERED'  |'COMPLETED'  |'CANCELLED' |'REJECTED' |'EXPIRED';
  userId?: string;
  userName?: string;
  taskId?: string;
  deliveryMethod?: 'PICKUP' | 'DELIVERY' | 'MARKET';
  deliveryIncluded?: boolean;
  proposedHarvestDate?: string ;
  createdAt?: string;
  updatedAt?: string; 
}