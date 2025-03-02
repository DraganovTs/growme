package com.home.growme.common.module.valuobjects;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class CategoryId extends BaseId<UUID> implements Serializable {

    protected CategoryId() {
        super();
    }

    protected CategoryId(UUID value) {
        super(value);
    }
}
