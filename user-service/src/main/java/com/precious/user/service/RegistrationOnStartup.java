package com.precious.user.service;

import com.precious.shared.kafka.KafkaTopics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegistrationOnStartup {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String serviceName;

    public RegistrationOnStartup(KafkaTemplate<String, String> kafkaTemplate,
                                 @Value("${app.service-name}") String serviceName) {
        this.kafkaTemplate = kafkaTemplate;
        this.serviceName = serviceName;
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        kafkaTemplate.send(KafkaTopics.SERVICE_REGISTRY, serviceName);
        System.out.println("✅ " + serviceName + " зарегистрирован в gateway.");
    }
}