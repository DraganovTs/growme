package com.home.order.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.model.entity.Owner;

/**
 * Service interface for managing Owner entities.
 * Provides methods for retrieving and creating owners based on system events or identifiers.
 */
public interface OwnerService {

    Owner findOwnerByEmail(String email);

    void createOwner(UserCreatedEvent event);


}
