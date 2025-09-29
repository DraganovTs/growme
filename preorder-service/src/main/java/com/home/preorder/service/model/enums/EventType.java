package com.home.preorder.service.model.enums;

public enum EventType {
    USER_CREATED("preorder.service.events.user.created", "user created"),
    CATEGORY_CREATION("preorder.service.events.category.creation", "category creation"),
    EMAIL_SEND("preorder.service.events.email.send", "email sending");

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
