package com.precious.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.precious.shared.dto.MetalPriceCheckRequest;
import com.precious.shared.dto.CurrencyPriceCheckRequest;
import com.precious.shared.kafka.KafkaTopics;
import com.precious.shared.model.Metal;
import com.precious.shared.model.Currency;
import com.precious.user.dto.PreferencesDto;
import com.precious.user.model.User;
import com.precious.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
public class AdminController {

    private final UserRepository userRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AdminController(
            UserRepository userRepository,
            NotificationLogRepository notificationLogRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.userRepository = userRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/api/admin/users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getEmail(), u.getPreferencesAsJson()))
                .toList();
    }

    @PostMapping("/api/admin/trigger-check")
    public ResponseEntity<Void> triggerPriceCheckForUser(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Object> requests = buildPriceCheckRequests(user);
        requests.forEach(req -> kafkaTemplate.send(KafkaTopics.PRICE_CHECK_REQUEST, req));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/admin/users/{email}/preferences")
    public ResponseEntity<?> updatePreferences(
            @PathVariable String email,
            @RequestBody JsonNode preferences
    ) {
        if (!PreferencesDto.isValid(preferences)) {
            return ResponseEntity.badRequest().body("Некорректный формат настроек");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPreferencesFromJson(preferences);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/admin/notifications")
    public List<NotificationLogDto> getNotificationLogs() {
        return notificationLogRepository.findAll().stream()
                .map(log -> new NotificationLogDto(
                        log.getUserEmail(),
                        log.getAssetName(),
                        log.getCurrentPrice(),
                        log.getTargetPrice(),
                        log.getOperation(),
                        log.getSentAt()
                ))
                .toList();
    }

    @GetMapping("/api/admin/notifications/export")
    public void exportNotificationsToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=notifications.csv");

        List<NotificationLog> logs = notificationLogRepository.findAll();
        try (PrintWriter writer = response.getWriter()) {
            writer.println("user_email,asset_name,current_price,target_price,operation,sent_at");
            for (NotificationLog log : logs) {
                writer.printf("%s,%s,%.4f,%.4f,%s,%s%n",
                        log.getUserEmail(),
                        log.getAssetName(),
                        log.getCurrentPrice(),
                        log.getTargetPrice(),
                        log.getOperation(),
                        log.getSentAt().toString()
                );
            }
        }
    }

    // --- Вспомогательные методы ---

    private List<Object> buildPriceCheckRequests(User user) {
        List<Object> requests = new ArrayList<>();
        // Используем безопасный getter для JSON
        JsonNode prefs = user.getPreferencesAsJson();

        // Металлы
        if (prefs.has("металлы")) {
            JsonNode metalsNode = prefs.get("металлы");
            if (metalsNode != null && metalsNode.isObject()) {
                Iterator<String> metalNames = metalsNode.fieldNames();
                while (metalNames.hasNext()) {
                    String metalName = metalNames.next();
                    JsonNode metalPrefs = metalsNode.get(metalName);
                    if (metalPrefs != null && metalPrefs.has("buyBelow")) {
                        Metal metal = getMetalByName(metalName);
                        MetalPriceCheckRequest req = new MetalPriceCheckRequest(
                                metal,
                                metalPrefs.get("buyBelow").asDouble(),
                                "buy",
                                user.getEmail()
                        );
                        requests.add(req);
                    }
                    if (metalPrefs != null && metalPrefs.has("sellAbove")) {
                        Metal metal = getMetalByName(metalName);
                        MetalPriceCheckRequest req = new MetalPriceCheckRequest(
                                metal,
                                metalPrefs.get("sellAbove").asDouble(),
                                "sell",
                                user.getEmail()
                        );
                        requests.add(req);
                    }
                }
            }
        }

        // Валюты
        if (prefs.has("валюты")) {
            JsonNode currenciesNode = prefs.get("валюты");
            if (currenciesNode != null && currenciesNode.isObject()) {
                Iterator<String> currencyCodes = currenciesNode.fieldNames();
                while (currencyCodes.hasNext()) {
                    String currencyCode = currencyCodes.next();
                    JsonNode currencyPrefs = currenciesNode.get(currencyCode);
                    if (currencyPrefs != null) {
                        try {
                            Currency currency = Currency.valueOf(currencyCode);
                            if (currencyPrefs.has("buyBelow")) {
                                CurrencyPriceCheckRequest req = new CurrencyPriceCheckRequest(
                                        currency,
                                        currencyPrefs.get("buyBelow").asDouble(),
                                        "buy",
                                        user.getEmail()
                                );
                                requests.add(req);
                            }
                            if (currencyPrefs.has("sellAbove")) {
                                CurrencyPriceCheckRequest req = new CurrencyPriceCheckRequest(
                                        currency,
                                        currencyPrefs.get("sellAbove").asDouble(),
                                        "sell",
                                        user.getEmail()
                                );
                                requests.add(req);
                            }
                        } catch (IllegalArgumentException e) {
                            // Игнорировать неизвестные валюты
                        }
                    }
                }
            }
        }

        return requests;
    }

    private Metal getMetalByName(String name) {
        return switch (name.toLowerCase()) {
            case "золото" -> Metal.GOLD;
            case "серебро" -> Metal.SILVER;
            case "платина" -> Metal.PLATINUM;
            default -> throw new IllegalArgumentException("Unknown metal: " + name);
        };
    }

    // --- DTO ---

    public static class UserDto {
        public String email;
        public JsonNode preferences;

        public UserDto(String email, JsonNode preferences) {
            this.email = email;
            this.preferences = preferences;
        }
    }

    public static class NotificationLogDto {
        public String userEmail;
        public String assetName;
        public double currentPrice;
        public double targetPrice;
        public String operation;
        public Instant sentAt;

        public NotificationLogDto(
                String userEmail,
                String assetName,
                double currentPrice,
                double targetPrice,
                String operation,
                Instant sentAt
        ) {
            this.userEmail = userEmail;
            this.assetName = assetName;
            this.currentPrice = currentPrice;
            this.targetPrice = targetPrice;
            this.operation = operation;
            this.sentAt = sentAt;
        }
    }
}