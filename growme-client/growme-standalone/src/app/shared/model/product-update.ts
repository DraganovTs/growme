import { ProductCreateDTO } from "./product-create";

export interface ProductUpdateDTO extends ProductCreateDTO {
    productId: string;
}