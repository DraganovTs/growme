package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.mapper.ProductMapper;
import com.home.growme.produt.service.model.dto.OwnerDTO;
import com.home.growme.produt.service.model.entity.Owner;
import com.home.growme.produt.service.repository.OwnerRepository;
import com.home.growme.produt.service.service.OwnerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final ProductMapper productMapper;

    public OwnerServiceImpl(OwnerRepository ownerRepository, ProductMapper productMapper) {
        this.ownerRepository = ownerRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<OwnerDTO> getAllOwners() {

        List<Owner> owners = ownerRepository.findAll();

        return owners.stream()
                .map(productMapper::maOwnerToOwnerDTO).limit(8).collect(Collectors.toList());
    }

    @Override
    public void createOwner(UserCreatedEvent event) {
        Owner owner = ProductMapper.mapUserCreatedEventToOwner(event);
        ownerRepository.save(owner);
    }
}
