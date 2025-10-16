package com.precious.shared.kafka;

public final class KafkaTopics {
    public static final String SERVICE_REGISTRY = "service.registry";
    public static final String PRICE_CHECK_REQUEST = "price.check.request";
    public static final String PRICE_CHECK_RESULT = "price.check.result";
    public static final String NOTIFICATION_EMAIL = "notification.email";

    private KafkaTopics() {}
}