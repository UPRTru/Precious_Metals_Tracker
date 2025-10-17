package com.precious.metal.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.precious.shared.model.CurrentPrice;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SberbankMetalClient {

    private final WebClient webClient;

    public SberbankMetalClient() {
        this.webClient = WebClient.builder()
//        https://www.sberbank.ru/ru/person/metall?metal=A98 золото 1г
//        https://www.sberbank.ru/ru/person/metall?metal=A99 серебро 50г
//        https://www.sberbank.ru/ru/person/metall?metal=A76 платина 5г
//        https://www.sberbank.ru/ru/person/metall?metal=A33 палладий 5г
                /*
                покупка
                * rates-ingots-table__tr
                * rates-ingots-table__td
                * dk-sbol-text dk-sbol-text_size_body2 rates-ingots-table__text
                * 12 609,00 ₽
                * */

                /*
                продажа
                * rates-ingots-table__tr
                * rates-ingots-table__td
                * dk-sbol-text dk-sbol-text_size_body2 rates-ingots-table__text
                * 10 664,00 ₽
                * */

                .baseUrl("https://api.sberbank.ru/prod/hackathon/public/info")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    private Mono<MetalItem> extractPriceFromResponse(JsonNode response, String metalDisplayName) {
        var rates = response.path("metalRates");
        for (JsonNode rate : rates) {
            String nameInResponse = rate.path("name").asText();
            if (matchesMetal(nameInResponse, metalDisplayName)) {
                return Mono.just(new MetalItem(metalDisplayName, rate.path("buy").asDouble(), rate.path("sell").asDouble()));
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

    public Mono<Double> getCurrentBuyPrice(String metalDisplayName, CurrentPrice currentPrice) {
        return webClient.get()
                .uri("/metalRates")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response -> extractPriceFromResponse(response, metalDisplayName)) // ← передаём параметр
                .map(m -> switch (currentPrice) {
                    case BUY -> m.getBuy();
                    case SELL -> m.getSell();
                })
                .onErrorReturn(0.0);
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

    private static class MetalItem {
        private final String name;
        private double buy;
        private double sell;

        public MetalItem(String name, double buy, double sell) {
            this.name = name;
            this.buy = buy;
            this.sell = sell;
        }

        public String getName() { return name; }
        public double getBuy() { return buy; }
        public double getSell() { return sell; }
    }
}