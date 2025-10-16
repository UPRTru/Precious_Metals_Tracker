package com.precious.currency.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
public class SberbankCurrencyClient {

    private final WebClient webClient;

    public SberbankCurrencyClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.sberbank.ru/prod/hackathon/public/info")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(5))
                ))
                .build();
    }

    public Mono<Double> getCurrentBuyPrice(String currencyCode) {
        return webClient.get()
                .uri("/currencyRates")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response -> extractPriceFromResponse(response, currencyCode))
                .onErrorReturn(getFallbackPrice(currencyCode));
    }

    private Mono<Double> extractPriceFromResponse(JsonNode response, String requestedCurrency) {
        var rates = response.path("currencyRates");
        for (JsonNode rate : rates) {
            if (rate.path("name").asText().equalsIgnoreCase(requestedCurrency)) {
                return Mono.just(rate.path("buy").asDouble());
            }
        }
        return Mono.error(new RuntimeException("Currency not found: " + requestedCurrency));
    }

    private double getFallbackPrice(String currencyCode) {
        return switch (currencyCode.toUpperCase()) {
            case "USD" -> 93.5;
            case "EUR" -> 101.0;
            case "GBP" -> 118.0;
            case "JPY" -> 0.62;
            default -> throw new IllegalArgumentException("Unknown currency: " + currencyCode);
        };
    }
}