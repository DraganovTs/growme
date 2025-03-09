package com.home.growme.produt.service.service.impl;

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
//        List<OwnerDTO> ownerDTOList = owners.stream().map(Owner::mapOwnerToOwnerDTO)
//                .collect(Collectors.toList());

        return null;
    }
}
