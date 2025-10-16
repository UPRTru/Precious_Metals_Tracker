package com.precious.gateway.kafka;

import com.precious.gateway.service.ServiceRegistry;
import com.precious.shared.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ServiceRegistryListener {

    private final ServiceRegistry serviceRegistry;

    public ServiceRegistryListener(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @KafkaListener(topics = KafkaTopics.SERVICE_REGISTRY, groupId = "gateway-group")
    public void handleServiceRegistration(String serviceName) {
        // Предполагаем, что serviceName — это имя сервиса, например "user-service"
        // В реальности можно передавать JSON с URL, но для простоты — имя
        String frontendUrl = "http://localhost:" + getServicePort(serviceName) + "/widget";
        serviceRegistry.register(serviceName, frontendUrl);
    }

    private int getServicePort(String serviceName) {
        return switch (serviceName) {
            case "user-service" -> 8081;
            case "metal-price-service" -> 8082;
            case "currency-price-service" -> 8083;
            default -> 8080;
        };
    }
}