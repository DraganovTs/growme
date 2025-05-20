package com.home.growme.common.module.config.kafka.topic;

public class KafkaTopics {

    //PAYMENT
    public static final String PAYMENT_INTENT_REQUESTS_TOPIC = "payment.intent.requests";
    public static final String PAYMENT_INTENT_RESPONSES_TOPIC = "payment.intent.responses";
    public static final String PAYMENT_FAILURES_TOPIC = "payment.failures";



    //USER REGISTRATION
    public static final String ROLE_ASSIGNMENT_TOPIC = "user.role.assignments";
    public static final String USER_ROLE_ASSIGNMENT_RESULT_TOPIC = "user.role.assignments.result";
    public static final String USER_CREATE_TOPIC = "user.created";


    //PRODUCT
    public static final String PRODUCT_ASSIGNMENT_TOPIC = "product.user.assignment";
    public static final String PRODUCT_DELETION_TOPIC = "product.user.deletion";


    //MAIL
    public static final String EMAIL_SEND_TOPIC = "email.send";


    //ORDER
    public static final String ORDER_COMPLETED_TOPIC = "order.completed";

}
