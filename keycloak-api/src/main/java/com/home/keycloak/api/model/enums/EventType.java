package com.home.keycloak.api.model.enums;

public enum EventType {
    ROLE_ASSIGNMENT("keycloak.service.events.role.assignment", "role assignment");


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

