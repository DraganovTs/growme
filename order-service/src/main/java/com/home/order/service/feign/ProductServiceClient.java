package com.home.order.service.feign;

import com.home.order.service.model.dto.BasketItemDTO;
import com.home.order.service.model.dto.ProductValidationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8087")
public interface ProductServiceClient {

    @PostMapping("/api/products/validate-basket-items")
    ResponseEntity<List<ProductValidationResult>> validateBasketItems(@RequestBody List<BasketItemDTO> items);

}
