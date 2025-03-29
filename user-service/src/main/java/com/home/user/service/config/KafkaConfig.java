package com.home.user.service.config;


import com.home.growme.common.module.dto.RoleAssignmentMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.retries:3}")
    private int retries;

    @Value("${kafka.producer.linger-ms:5}")
    private int lingerMs;

    @Bean
    public ProducerFactory<String, RoleAssignmentMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG,retries);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG,lingerMs);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);
        configProps.put(ProducerConfig.ACKS_CONFIG,"all");

        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, RoleAssignmentMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
