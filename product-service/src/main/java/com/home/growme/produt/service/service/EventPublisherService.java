package com.home.growme.produt.service.service;

public interface EventPublisherService {

    void publishProductAssignment(String userId, String productId);
    void publishProductDeletion(String productId, String ownerId);
}
