package com.home.order.service.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface EventHandlerService {

    void handlePaymentIntentResponse(ConsumerRecord<String, String> record);

    void handlePaymentFailure(ConsumerRecord<String, String> record);
}
