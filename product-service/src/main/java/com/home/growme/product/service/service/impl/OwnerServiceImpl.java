package com.home.growme.product.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.product.service.exception.OwnerAlreadyExistsException;
import com.home.growme.product.service.mapper.ProductMapper;
import com.home.growme.product.service.model.dto.OwnerDTO;
import com.home.growme.product.service.model.entity.Owner;
import com.home.growme.product.service.repository.OwnerRepository;
import com.home.growme.product.service.service.OwnerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
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
                .map(productMapper::mapOwnerToOwnerDTO).limit(8).collect(Collectors.toList());
    }

    @Override
    public void createOwner(UserCreatedEvent event) {
        if (ownerRepository.existsById(UUID.fromString(event.getUserId()))) {
            throw new OwnerAlreadyExistsException(event.getUserId());
        }
        Owner owner = productMapper.mapUserCreatedEventToOwner(event);
        ownerRepository.save(owner);
    }

    @Override
    public List<OwnerDTO> getAllOwnersSortedByProductCount() {
        Pageable topEight = PageRequest.of(0, 8); // Get top 8 owners
        return ownerRepository.findAllOrderByProductCountDesc(topEight)
                .stream()
                .map(productMapper::mapOwnerToOwnerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserId(String userId) {
        return ownerRepository.existsById(UUID.fromString(userId));
    }
}
