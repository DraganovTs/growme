package com.home.growme.product.service.service;

/**
 * Interface defining the contract for event publishing related to product assignments and deletions.
 * This service is responsible for sending specific events to the appropriate event processing systems.
 */
public interface EventPublisherService {

    void publishProductAssignment(String userId, String productId);
    void publishProductDeletion(String productId, String ownerId);
    void publishCategoryCreation(String categoryId, String categoryName);
}
