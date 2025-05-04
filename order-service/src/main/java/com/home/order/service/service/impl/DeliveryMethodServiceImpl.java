package com.home.order.service.service.impl;

import com.home.order.service.mapper.DeliveryMethodMapper;
import com.home.order.service.model.dto.DeliveryMethodDTO;
import com.home.order.service.model.entity.DeliveryMethod;
import com.home.order.service.repository.DeliveryMethodRepository;
import com.home.order.service.service.DeliveryMethodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryMethodServiceImpl implements DeliveryMethodService {

    private final DeliveryMethodRepository deliveryMethodRepository;
    private final DeliveryMethodMapper deliveryMethodMapper;

    public DeliveryMethodServiceImpl(DeliveryMethodRepository deliveryMethodRepository, DeliveryMethodMapper deliveryMethodMapper) {
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    @Override
    public List<DeliveryMethodDTO> getAllDeliveryMethods() {
        List<DeliveryMethodDTO> dtos = deliveryMethodRepository.findAll().stream()
                .map(deliveryMethodMapper::mapDeliveryMethodToDeliveryMethodDTO)
                .toList();

        return dtos;
    }
}
