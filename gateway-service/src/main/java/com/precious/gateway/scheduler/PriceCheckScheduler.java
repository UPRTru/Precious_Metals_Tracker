package com.precious.gateway.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.precious.gateway.client.UserServiceClient;
import com.precious.shared.dto.CurrencyPriceCheckRequest;
import com.precious.shared.dto.MetalPriceCheckRequest;
import com.precious.shared.kafka.KafkaTopics;
import com.precious.shared.model.Currency;
import com.precious.shared.model.Metal;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class PriceCheckScheduler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    public PriceCheckScheduler(KafkaTemplate<String, Object> kafkaTemplate,
                               UserServiceClient userServiceClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.userServiceClient = userServiceClient;
    }

    @Scheduled(fixedRate = 30000) // каждые 30 сек
    public void checkPrices() {
        try {
            // Получаем всех пользователей с их настройками
            List<UserServiceClient.UserDto> users = userServiceClient.getAllUsers()
                    .collectList()
                    .block(Duration.ofSeconds(5));

            if (users == null || users.isEmpty()) {
                return;
            }

            List<Object> allRequests = new ArrayList<>();

            for (UserServiceClient.UserDto user : users) {
                JsonNode prefs = user.preferences;
                String email = user.email;

                // Обработка металлов
                if (prefs != null && prefs.has("металлы")) {
                    JsonNode metals = prefs.get("металлы");
                    Iterator<String> metalNames = metals.fieldNames();
                    while (metalNames.hasNext()) {
                        String metalName = metalNames.next();
                        JsonNode rules = metals.get(metalName);

                        if (rules.has("buyBelow")) {
                            Metal metal = getMetalByName(metalName);
                            MetalPriceCheckRequest req = new MetalPriceCheckRequest(metal, rules.get("buyBelow").asDouble(), "buy", email);
                            allRequests.add(req);
                        }
                        if (rules.has("sellAbove")) {
                            Metal metal = getMetalByName(metalName);
                            MetalPriceCheckRequest req = new MetalPriceCheckRequest(metal, rules.get("sellAbove").asDouble(), "sell", email);
                            allRequests.add(req);
                        }
                    }
                }

                // Обработка валют
                if (prefs != null && prefs.has("валюты")) {
                    JsonNode currencies = prefs.get("валюты");
                    Iterator<String> currencyCodes = currencies.fieldNames();
                    while (currencyCodes.hasNext()) {
                        String code = currencyCodes.next();
                        JsonNode rules = currencies.get(code);

                        try {
                            Currency currency = Currency.valueOf(code);
                            if (rules.has("buyBelow")) {
                                CurrencyPriceCheckRequest req = new CurrencyPriceCheckRequest(currency, rules.get("buyBelow").asDouble(), "buy", email);
                                allRequests.add(req);
                            }
                            if (rules.has("sellAbove")) {
                                CurrencyPriceCheckRequest req = new CurrencyPriceCheckRequest(currency, rules.get("sellAbove").asDouble(), "sell", email);
                                allRequests.add(req);
                            }
                        } catch (IllegalArgumentException e) {
                            // Игнорируем неизвестные валюты
                        }
                    }
                }
            }

            // Отправка всех запросов
            for (Object request : allRequests) {
                if (request instanceof MetalPriceCheckRequest) {
                    kafkaTemplate.send(KafkaTopics.PRICE_CHECK_REQUEST, "metal", request);
                } else if (request instanceof CurrencyPriceCheckRequest) {
                    kafkaTemplate.send(KafkaTopics.PRICE_CHECK_REQUEST, "currency", request);
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при проверке цен: " + e.getMessage());
        }
    }

    private Metal getMetalByName(String name) {
        return switch (name.toLowerCase()) {
            case "золото" -> Metal.GOLD;
            case "серебро" -> Metal.SILVER;
            case "платина" -> Metal.PLATINUM;
            default -> throw new IllegalArgumentException("Неизвестный металл: " + name);
        };
    }
}