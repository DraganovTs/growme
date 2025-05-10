package com.home.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface EventHandlerService {

    void handlePaymentRequest(ConsumerRecord<String, String> record);
}
