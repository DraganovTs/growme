package com.home.growme.produt.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.model.dto.OwnerDTO;

import java.util.List;

/**
 * Service interface for managing owner-related operations.
 * Provides methods to retrieve owner information and create new owners.
 */
public interface OwnerService {
    List<OwnerDTO> getAllOwners();
    void createOwner(UserCreatedEvent userCreatedEvent);
    List<OwnerDTO> getAllOwnersSortedByProductCount();

    boolean existsByUserId(String userId);
}
