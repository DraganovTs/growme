package com.home.preorder.service.model.enums;

public enum BidStatus {
    PENDING,        // Initial state after creation
    ACCEPTED,       // Buyer selected this offer
    CONFIRMED,      // Grower confirmed they can fulfill (optional but recommended)
    IN_PROGRESS,    // Grower started working on the order
    READY,          // Produce is ready for delivery/pickup
    SHIPPED,        // For delivery method
    DELIVERED,      // Successful delivery completion
    COMPLETED,      // Final successful state
    CANCELLED,      // Either party cancelled before completion
    REJECTED,       // Buyer declined the offer
    EXPIRED,         // Auto-rejected after timeframe
    COUNTER_OFFER
}
