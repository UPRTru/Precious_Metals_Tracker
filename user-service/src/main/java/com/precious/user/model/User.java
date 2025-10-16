package com.precious.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String EMPTY_JSON = "{}";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @Column(columnDefinition = "json")
    private String metalBuyHistory = EMPTY_JSON;

    @Column(columnDefinition = "json")
    private String metalSellHistory = EMPTY_JSON;

    @Column(columnDefinition = "json")
    private String currencyBuyHistory = EMPTY_JSON;

    @Column(columnDefinition = "json")
    private String currencySellHistory = EMPTY_JSON;

    @Column(columnDefinition = "json")
    private String preferences = EMPTY_JSON;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt = Instant.now();

    // Constructors
    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // --- Getters for raw String (for JPA) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMetalBuyHistory() { return metalBuyHistory; }
    public void setMetalBuyHistory(String metalBuyHistory) { this.metalBuyHistory = metalBuyHistory; }

    public String getMetalSellHistory() { return metalSellHistory; }
    public void setMetalSellHistory(String metalSellHistory) { this.metalSellHistory = metalSellHistory; }

    public String getCurrencyBuyHistory() { return currencyBuyHistory; }
    public void setCurrencyBuyHistory(String currencyBuyHistory) { this.currencyBuyHistory = currencyBuyHistory; }

    public String getCurrencySellHistory() { return currencySellHistory; }
    public void setCurrencySellHistory(String currencySellHistory) { this.currencySellHistory = currencySellHistory; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    // --- JSON Helpers (safe, immutable view) ---

    public JsonNode getMetalBuyHistoryAsJson() {
        return parseJson(metalBuyHistory);
    }

    public JsonNode getMetalSellHistoryAsJson() {
        return parseJson(metalSellHistory);
    }

    public JsonNode getCurrencyBuyHistoryAsJson() {
        return parseJson(currencyBuyHistory);
    }

    public JsonNode getCurrencySellHistoryAsJson() {
        return parseJson(currencySellHistory);
    }

    public JsonNode getPreferencesAsJson() {
        return parseJson(preferences);
    }

    public void setMetalBuyHistoryFromJson(JsonNode node) {
        this.metalBuyHistory = toJson(node);
    }

    public void setMetalSellHistoryFromJson(JsonNode node) {
        this.metalSellHistory = toJson(node);
    }

    public void setCurrencyBuyHistoryFromJson(JsonNode node) {
        this.currencyBuyHistory = toJson(node);
    }

    public void setCurrencySellHistoryFromJson(JsonNode node) {
        this.currencySellHistory = toJson(node);
    }

    public void setPreferencesFromJson(JsonNode node) {
        this.preferences = toJson(node);
    }

    private static JsonNode parseJson(String json) {
        if (json == null || json.isEmpty() || "{}".equals(json)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON: " + json, e);
        }
    }

    private static String toJson(JsonNode node) {
        if (node == null || node.isEmpty()) {
            return EMPTY_JSON;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize JSON node", e);
        }
    }
}