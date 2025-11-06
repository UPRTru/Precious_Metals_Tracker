package com.precious.general.client;

import com.precious.shared.model.Banks;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class BankAgentClient {

    @Value("${bank.agent.url}")
    private String bankAgentUrl;

    private final WebClient webClient;

    public BankAgentClient() {
        this.webClient = WebClient.create(bankAgentUrl);
    }

    public Flux<JSONArray> getAllMetal(Banks bank) {
        return webClient.get()
                .uri("/" + bank.name().toLowerCase() + "/metal/all")
                .retrieve()
                .bodyToFlux(JSONArray.class);
    }

    public Flux<JSONArray> getAllCurrency(Banks bank) {
        return webClient.get()
                .uri("/" + bank.name().toLowerCase() + "/currency/all")
                .retrieve()
                .bodyToFlux(JSONArray.class);
    }

    public Flux<JSONObject> getLatestMetal(Banks bank, String metalName) {
        return webClient.get()
                .uri("/" + bank.name().toLowerCase() + "/metal/lastprice/?metalName=" + metalName)
                .retrieve()
                .bodyToFlux(JSONObject.class);
    }

    public Flux<JSONObject> getLatestCurrency(Banks bank, String currencyName) {
        return webClient.get()
                .uri("/" + bank.name().toLowerCase() + "/currency/lastprice/?currencyName=" + currencyName)
                .retrieve()
                .bodyToFlux(JSONObject.class);
    }


    public Flux<JSONArray> getHistoryMetal(Banks bank, String metalName, long from, long to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{bank}/metal/history/{metalName}")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build(bank.name().toLowerCase(), metalName))
                .retrieve()
                .bodyToFlux(JSONArray.class);
    }

    public Flux<JSONArray> getHistoryCurrency(Banks bank, String currencyName, long from, long to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{bank}/currency/history/{currencyName}")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build(bank.name().toLowerCase(), currencyName))
                .retrieve()
                .bodyToFlux(JSONArray.class);
    }
}