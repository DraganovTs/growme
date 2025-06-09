package com.home.order.service.feign;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.dto.ProductValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class ProductServiceFallback implements ProductServiceClient{


    @Override
    public ResponseEntity<List<ProductValidationResult>> validateBasketItems(String correlationId, List<BasketItemDTO> items) {
        log.error("Fallback: Unable to validate basket items for correlationId {}", correlationId);

        List<ProductValidationResult> fallbackResults = items.stream()
                .map(item -> new ProductValidationResult(item.getProductId(), false, "Product validation unavailable"))
                .toList();

        return ResponseEntity.ok(fallbackResults);
    }

    @Override
    public ProductInfo getProductInfo(String correlationId, String productId) {
        log.error("Fallback: Unable to fetch product info for productId: {}, correlationId: {}", productId, correlationId);

        return ProductInfo.builder()
                .id(productId)
                .name("unknown name for product")
                .imageUrl("unknown imageUrl for product")
                .price(new BigDecimal(0))
                .build();


    }
}
