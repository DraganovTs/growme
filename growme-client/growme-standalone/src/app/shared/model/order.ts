export interface IAddress {
    firstName: string;
    lastName: string;
    street: string;
    city: string;
    state: string;
    zipCode: string;
}

export interface IOrderToCreate {
    basketId: string;
    deliveryMethodId: number;
    shipToAddress: IAddress;
    userEmail: string;
}

export interface IOrderReturned {
    basketId: string;
    deliveryMehtodId: number;
    shipToAddress: IAddress;
}


export interface IOrder {
    orderId: number;
    buyerEmail: string;
    orderDate: string;
    shipToAddress: IAddress;
    deliveryMethod: string;
    shippingPrice: number;
    orderItems: IOrderItem[];
    subTotal: number;
    total: number;
    status: string;
}

export interface IOrderItem{
    productId: string;
    productName: string;
    imageUrl: string;
    price: number;
    quantity: number;
}