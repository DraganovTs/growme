export interface ITask {
  id: string;
  title: string;
  description: string;
  status: TaskStatus;
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


export type TaskStatus =
  |'PENDING' 
  | 'ACTIVE' 
  | 'COMPLETED' 
  | 'CANCELLED' 
  | 'OPEN';

