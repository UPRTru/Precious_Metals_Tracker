package com.precious.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.precious.user.model.User;
import com.precious.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public TransactionController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/metal/buy")
    public void recordMetalBuy(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request
    ) {
        recordMetalTransaction(userDetails.getUsername(), request, "buy");
    }

    @PostMapping("/metal/sell")
    public void recordMetalSell(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request
    ) {
        recordMetalTransaction(userDetails.getUsername(), request, "sell");
    }

    @PostMapping("/currency/buy")
    public void recordCurrencyBuy(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request
    ) {
        recordCurrencyTransaction(userDetails.getUsername(), request, "buy");
    }

    @PostMapping("/currency/sell")
    public void recordCurrencySell(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request
    ) {
        recordCurrencyTransaction(userDetails.getUsername(), request, "sell");
    }

    private void recordMetalTransaction(String email, TransactionRequest request, String type) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        ObjectNode history = (ObjectNode) ("buy".equals(type)
                ? user.getMetalBuyHistoryAsJson()
                : user.getMetalSellHistoryAsJson());

        if (!history.has(request.asset)) {
            history.set(request.asset, objectMapper.createArrayNode());
        }
        ArrayNode array = (ArrayNode) history.get(request.asset);
        ObjectNode record = objectMapper.createObjectNode();
        record.put("date", LocalDate.now().toString());
        record.put("price", request.price);
        array.add(record);

        if ("buy".equals(type)) {
            user.setMetalBuyHistoryFromJson(history);
        } else {
            user.setMetalSellHistoryFromJson(history);
        }

        userService.saveUser(user);
    }

    private void recordCurrencyTransaction(String email, TransactionRequest request, String type) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        ObjectNode history = (ObjectNode) ("buy".equals(type)
                ? user.getCurrencyBuyHistoryAsJson()
                : user.getCurrencySellHistoryAsJson());

        if (!history.has(request.asset)) {
            history.set(request.asset, objectMapper.createArrayNode());
        }
        ArrayNode array = (ArrayNode) history.get(request.asset);
        ObjectNode record = objectMapper.createObjectNode();
        record.put("date", LocalDate.now().toString());
        record.put("price", request.price);
        array.add(record);

        if ("buy".equals(type)) {
            user.setCurrencyBuyHistoryFromJson(history);
        } else {
            user.setCurrencySellHistoryFromJson(history);
        }

        userService.saveUser(user);
    }

    public static class TransactionRequest {
        @NotBlank
        public String asset;
        @NotNull
        public Double price;
    }
}