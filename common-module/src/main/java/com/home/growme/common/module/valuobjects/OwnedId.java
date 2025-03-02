package com.home.growme.common.module.valuobjects;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class OwnedId extends BaseId<UUID> implements Serializable {

    public OwnedId() {
        super();
    }

    protected OwnedId(UUID value) {
        super(value);
    }
}
