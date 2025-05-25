package com.home.order.service.service;

import com.home.order.service.model.dto.DeliveryMethodDTO;

import java.util.List;

/**
 * Service interface responsible for handling operations related to delivery methods.
 * Provides functionality to retrieve delivery method details available in the system.
 */
public interface DeliveryMethodService {

   List<DeliveryMethodDTO> getAllDeliveryMethods();
}
