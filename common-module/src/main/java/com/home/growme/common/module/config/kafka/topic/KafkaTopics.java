package com.home.growme.common.module.config.kafka.topic;

public class KafkaTopics {

    //PAYMENT
    public static final String PAYMENT_INTENT_REQUESTS = "payment.intent.requests";
    public static final String PAYMENT_INTENT_RESPONSES = "payment.intent.responses";
    public static final String PAYMENT_FAILURES = "payment.failures";



    //USER REGISTRATION
    public static final String ROLE_ASSIGNMENT = "user.role.assignments";
    public static final String USER_ROLE_ASSIGNMENT_RESULT = "user.role.assignments.result";
    public static final String USER_CREATE = "user.created";


    //PRODUCT
    public static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
    public static final String PRODUCT_DELETION_TOPIC = "product.user.deletion";

}
