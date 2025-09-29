package com.home.order.service.model.enums;

public enum EventType {

    PAYMENT_INTENT_RESPONSE("order.service.events.payment.intent.response", "payment intent response"),
    PAYMENT_FAILURE("order.service.events.payment.failure", "payment failure"),
    USER_CREATED("order.service.events.user.created", "user created"),
    ORDER_COMPLETED("order.service.events.order.completed", "order completed"),
    EMAIL_SEND("order.service.events.email.send", "email sending");

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
