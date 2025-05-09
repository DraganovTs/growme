package com.home.growme.common.module.config.kafka.topic;

public class KafkaTopics {

    //PAYMENT
    public static final String PAYMENT_INTENT_REQUEST = "payment.intent.request";
    public static final String PAYMENT_INTENT_RESPONSE = "payment.intent.response";
    public static final String PAYMENT_INTENT_UPDATE = "payment.intent.update";
    public static final String PAYMENT_FAILURE_EVENTS = "payment.failure.event";



    //USER REGISTRATION
    public static final String ROLE_ASSIGNMENT = "user.role.assignments";
    public static final String USER_ROLE_ASSIGNMENT_RESULT = "user.role.assignments.result";
    public static final String USER_CREATE = "user.created";


    //PRODUCT
    public static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
    public static final String PRODUCT_DELETION_TOPIC = "product.user.deletion";

}
