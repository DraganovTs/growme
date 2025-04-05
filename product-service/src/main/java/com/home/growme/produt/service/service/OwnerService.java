package com.home.growme.produt.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.model.dto.OwnerDTO;

import java.util.List;

public interface OwnerService {
    List<OwnerDTO> getAllOwners();
    void createOwner(UserCreatedEvent userCreatedEvent);
}
