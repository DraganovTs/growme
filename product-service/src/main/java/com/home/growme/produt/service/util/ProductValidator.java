package com.home.growme.produt.service.util;

import com.home.growme.produt.service.exception.ProductRequestNotValidNameException;
import com.home.growme.produt.service.model.dto.ProductRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validateProductRequest(ProductRequestDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ProductRequestNotValidNameException("Product name is required!");
        }
        // Add more validations as needed
    }
}
