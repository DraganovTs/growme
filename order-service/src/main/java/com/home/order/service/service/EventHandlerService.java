package com.home.order.service.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface EventHandlerService {

    void handlePaymentResponse(ConsumerRecord<String, Object> record);
    void handlePaymentFailure(ConsumerRecord<String, String> record);
}
