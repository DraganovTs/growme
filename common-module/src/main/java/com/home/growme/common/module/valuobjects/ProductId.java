package com.home.growme.common.module.valuobjects;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ProductId extends BaseId<UUID> implements Serializable {

    protected ProductId(){
        super();
    }

    protected ProductId(UUID value) {
        super(value);
    }
}
