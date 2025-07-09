export interface Bid {
  id: string;
  price: number;
  message: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  userId?: string;
  growerName?: string;
  taskId?: string;
  deliveryMethod?: string;
  deliveryIncluded?: boolean;
  proposedHarvestDate?: string | Date;
  createdAt?: string | Date;
  updatedAt?: string | Date; 
}