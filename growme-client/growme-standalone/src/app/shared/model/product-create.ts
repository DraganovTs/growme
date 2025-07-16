export interface IProductCreateDTO {
    name: string;
    brand?: string | null;
    description: string;
    price: number;
    unitsInStock: number;
    imageUrl?: string | null;
    categoryName: string;
    ownerId: string;
}

export interface ImageUploadResponse {
    url: string;
  }


  export interface ProductUpdateDTO extends IProductCreateDTO {
    productId: string;
  }