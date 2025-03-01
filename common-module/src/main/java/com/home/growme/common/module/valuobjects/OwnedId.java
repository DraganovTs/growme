package com.home.growme.common.module.valuobjects;

import java.util.UUID;

public class OwnedId extends BaseId<UUID> {
    protected OwnedId(UUID value) {
        super(value);
    }
}
