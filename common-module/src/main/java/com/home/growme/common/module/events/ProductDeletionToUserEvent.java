package com.home.growme.common.module.events;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProductDeletionToUserEvent extends Event {
    private final String userId;
    private final String productId;

    public ProductDeletionToUserEvent(@JsonProperty("userId") String userId,
                                     @JsonProperty("productId") String productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
