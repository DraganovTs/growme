package com.home.user.service.model.enums;

public enum EventType {
    ROLE_ASSIGNMENT("user.service.events.role.assignment", "role assignment"),
    PRODUCT_ASSIGNMENT("user.service.events.product.assignment", "product assignment"),
    PRODUCT_DELETION("user.service.events.product.deletion", "product deletion"),
    ORDER_COMPLETED("user.service.events.order.completed", "order completed"),
    USER_CREATED("user.service.events.user.created", "user created"),
    EMAIL_SEND("user.service.events.email.send", "email sending");

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

