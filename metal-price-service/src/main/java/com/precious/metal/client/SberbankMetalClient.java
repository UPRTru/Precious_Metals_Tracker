package com.precious.metal.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class SberbankMetalClient {

    private final WebClient webClient;

    public SberbankMetalClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.sberbank.ru/prod/hackathon/public/info")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    private Mono<Double> extractPriceFromResponse(JsonNode response, String metalDisplayName) {
        var rates = response.path("metalRates");
        for (JsonNode rate : rates) {
            String nameInResponse = rate.path("name").asText();
            // Сопоставляем displayName (например, "золото") с ответом ("Золото")
            if (matchesMetal(nameInResponse, metalDisplayName)) {
                return Mono.just(rate.path("buy").asDouble());
            }
        }
        return Mono.error(new RuntimeException("Металл не найден: " + metalDisplayName));
    }

    private boolean matchesMetal(String apiName, String displayName) {
        return switch (displayName.toLowerCase()) {
            case "золото" -> apiName.equalsIgnoreCase("Золото");
            case "серебро" -> apiName.equalsIgnoreCase("Серебро");
            case "платина" -> apiName.equalsIgnoreCase("Платина");
            default -> false;
        };
    }

    public Mono<Double> getCurrentBuyPrice(String metalDisplayName) {
        return webClient.get()
                .uri("/metalRates")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response -> extractPriceFromResponse(response, metalDisplayName)) // ← передаём параметр
                .onErrorReturn(getFallbackPrice(metalDisplayName));
    }

    private Mono<Double> extractPriceFromResponse(JsonNode response) {
        // Пример структуры:
        // { "metalRates": [ { "name": "Золото", "buy": 6500.5, "sell": 6400.0 } ] }
        var rates = response.path("metalRates");
        for (JsonNode rate : rates) {
            if (rate.path("name").asText().equalsIgnoreCase("Золото") && "золото".equalsIgnoreCase("золото")) {
                return Mono.just(rate.path("buy").asDouble());
            }
            // Добавьте другие металлы по аналогии
        }
        return Mono.error(new RuntimeException("Металл не найден"));
    }

    private double getFallbackPrice(String metalDisplayName) {
        return switch (metalDisplayName.toLowerCase()) {
            case "золото" -> 6500.0;
            case "серебро" -> 85.0;
            case "платина" -> 2200.0;
            default -> 0.0;
        };
    }
}