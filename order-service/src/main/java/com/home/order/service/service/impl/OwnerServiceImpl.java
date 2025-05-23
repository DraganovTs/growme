package com.home.order.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.exception.OwnerNotFoundException;
import com.home.order.service.model.entity.Owner;
import com.home.order.service.repository.OwnerRepository;
import com.home.order.service.service.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Owner findOwnerByEmail(String email) {
        return ownerRepository.findOwnerByOwnerEmail(email).orElseThrow(
                () -> new OwnerNotFoundException("owner not found whit email: " + email));
    }

    @Override
    public void createOwner(UserCreatedEvent event) {
        Owner owner = new Owner(UUID.fromString(event.getUserId()), event.getUserName(),
                event.getUserEmail(), new ArrayList<>());
        ownerRepository.save(owner);
    }
}
