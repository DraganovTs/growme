package com.home.order.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDTO {
    @JsonProperty("basketId")
    private String basketId;
    @JsonProperty("deliveryMethodId")
    private int deliveryMethodId;
    @JsonProperty("shipToAddress")
    private AddressDTO shipToAddress;
    @JsonProperty("userEmail")
    private String userEmail;
}
