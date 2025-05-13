package com.home.order.service.feign;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.common.module.dto.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8087")
public interface ProductServiceClient {

    @PostMapping("/api/products/validate")
    ResponseEntity<List<ProductValidationResult>> validateBasketItems(@RequestBody List<BasketItemDTO> items);

    @GetMapping("/api/products/productinfo/{productId}")
    ProductInfo getProductInfo(@PathVariable String productId);

}
