export interface ProductCreateDTO {
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


  export interface ProductUpdateDTO extends ProductCreateDTO {
    productId: string;
  }