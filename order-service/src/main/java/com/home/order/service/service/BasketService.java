package com.home.order.service.service;

import com.home.order.service.model.dto.BasketData;

/**
 * Interface defining the contract for basket-related operations. The BasketService is responsible
 * for managing basket entities including creation, updates, retrieval, and deletion.
 */
public interface BasketService {

    BasketData createOrUpdateBasket(BasketData basketData);
    BasketData getBasketById(String id);
    void deleteBasket(String id);
}
