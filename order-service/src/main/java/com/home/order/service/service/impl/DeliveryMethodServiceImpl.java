package com.home.order.service.service.impl;

import com.home.order.service.model.entity.DeliveryMethod;
import com.home.order.service.repository.DeliveryMethodRepository;
import com.home.order.service.service.DeliveryMethodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryMethodServiceImpl implements DeliveryMethodService {

    private final DeliveryMethodRepository deliveryMethodRepository;

    public DeliveryMethodServiceImpl(DeliveryMethodRepository deliveryMethodRepository) {
        this.deliveryMethodRepository = deliveryMethodRepository;
    }

    @Override
    public List<DeliveryMethod> getAllDeliveryMethods() {
        return deliveryMethodRepository.findAll();
    }
}
