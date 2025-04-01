export interface ProductCreateDTO {
    name: string;
    brand?: string;
    description: string;
    price: number;
    unitsInStock: number;
    imageUrl?: string;
    categoryId: string;
    ownerId: string;
}

export interface ImageUploadResponse {
    url: string;
  }