export interface Task {
  id: string;
  title: string;
  description: string;
  status: 'PENDING' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  userId: string;
  user?: {
    username: string;
  };
  category: string;
  quantity: number;
  unit: string;
  harvestDate: string;
  deliveryLocation: string;
  maxPrice?: number;
  createdAt: string;
  updatedAt: string;
  
}

export interface Bid {
  id: string;
  price: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  growerId: string;
  growerName: string;
  proposedHarvestDate: string;
  deliveryMethod: string;
  deliveryIncluded: boolean;
  message: string;
  createdAt: string;
  growerRating?: number;
  growerRatingCount?: number;
}