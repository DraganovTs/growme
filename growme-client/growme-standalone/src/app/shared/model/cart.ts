import{createId} from '@paralleldrive/cuid2'

export interface ICart {
    id: string;
    items: ICartItem[];
    shippingPrice: number;
    deliveryMethodId?: number;
    clientSecret?: string;
    paymentIntentId?: string;
}



export interface ICartItem {
    productId: string;
    name: string;
    unitsInStock: number;
    quantity: number;
    imageUrl: string;
    price: number;
    brandName: string;
    categoryName: string;
}

export class Cart implements ICart {
    id: string;
    items: ICartItem[] = [];
    shippingPrice = 0;
    deliveryMethodId=0;

    constructor() {
        this.id = createId();
      }
    
}

export interface ICartTotals {
    subtotal: number;
    shipping: number;
    total: number;
}



