import{createId} from '@paralleldrive/cuid2'

export interface ICart {
    id: string;
    items: ICartItem[];
    shippingPrice: number;
}



export interface ICartItem {
    id: string;
    name: string;
    price: number;
    quantity: number;
    image: string;
    brandName: string;
    categoryName: string;
}

export class Cart implements ICart {
    id: string;
    items: ICartItem[] = [];
    shippingPrice = 0;

    constructor() {
        this.id = createId();
      }
    
}

export interface ICartTotals {
    subtotal: number;
    shipping: number;
    total: number;
}



