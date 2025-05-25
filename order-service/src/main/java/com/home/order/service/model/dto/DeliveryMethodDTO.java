package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "DeliveryMethodDTO", description = "Details about a delivery method")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMethodDTO {
    @Schema(description = "Unique delivery method ID", example = "1")
    private Integer deliveryMethodId;
    @Schema(description = "Short name of delivery method", example = "Express")
    private String shortName;
    @Schema(description = "Estimated delivery time", example = "1-2 business days")
    private String deliveryTime;
    @Schema(description = "Description of delivery method", example = "Fast delivery with tracking")
    private String description;
    @Schema(description = "Price of the delivery method", example = "9.99")
    private BigDecimal price;
}
