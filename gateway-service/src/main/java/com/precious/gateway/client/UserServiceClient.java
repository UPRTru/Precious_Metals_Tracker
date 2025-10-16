package com.precious.gateway.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    private final WebClient webClient;

    public UserServiceClient() {
        this.webClient = WebClient.create(userServiceUrl);
    }

    public Flux<UserDto> getAllUsers() {
        return webClient.get()
                .uri("/api/admin/users")
                .retrieve()
                .bodyToFlux(UserDto.class);
    }

    public Mono<Void> triggerPriceCheckForUser(String email) {
        return webClient.post()
                .uri("/api/admin/trigger-check?email=" + email)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> updatePreferences(String email, JsonNode preferences) {
        return webClient.post()
                .uri("/api/admin/users/" + email + "/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(preferences)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public static class UserDto {
        public String email;
        public JsonNode preferences;
        // getters/setters не обязательны для Jackson
    }
}