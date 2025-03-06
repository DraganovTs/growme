export interface IProduct {
    productId: string;
    name: string;
    brand: string;
    description: string;
    price: number;
    unitsInStock: number;
    imageUrl: string;
    categoryName: string;
    ownerName: string;
    ownerId: string;
    productCategoryId: string;
}


export interface IOwner{
    ownerId: string;
    ownerName: string;
}

export interface ICategory{
    categoryId: string;
    categoryName: string;
}