package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.exception.OwnerAlreadyExistsException;
import com.home.growme.produt.service.mapper.ProductMapper;
import com.home.growme.produt.service.model.dto.OwnerDTO;
import com.home.growme.produt.service.model.entity.Owner;
import com.home.growme.produt.service.repository.OwnerRepository;
import com.home.growme.produt.service.service.OwnerService;
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
                .map(productMapper::maOwnerToOwnerDTO).limit(8).collect(Collectors.toList());
    }

    @Override
    public void createOwner(UserCreatedEvent event) {
        if (ownerRepository.existsById(UUID.fromString(event.getUserId()))) {
            throw new OwnerAlreadyExistsException(event.getUserId());
        }
        Owner owner = ProductMapper.mapUserCreatedEventToOwner(event);
        ownerRepository.save(owner);
    }

    @Override
    public List<OwnerDTO> getAllOwnersSortedByProductCount() {
        Pageable topEight = PageRequest.of(0, 8); // Get top 8 owners
        return ownerRepository.findAllOrderByProductCountDesc(topEight)
                .stream()
                .map(productMapper::maOwnerToOwnerDTO)
                .collect(Collectors.toList());
    }
}
