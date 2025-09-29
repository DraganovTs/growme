package com.home.growme.product.service.model.enums;

public enum EventType {

    PRODUCT_ASSIGNMENT("product.service.events.product.assignment", "product assignment"),
    PRODUCT_DELETION("product.service.events.product.deletion", "product deletion"),
    CATEGORY_CREATION("product.service.events.category.creation", "category creation"),
    USER_CREATED("product.service.events.user.created", "user created"),
    ORDER_COMPLETED("product.service.events.order.completed", "order completed");
    private final String metricName;
    private final String description;

    EventType(String metricName, String description) {
        this.metricName = metricName;
        this.description = description;
    }

    public String metricName() {
        return metricName;
    }

    public String description() {
        return description;
    }
}
