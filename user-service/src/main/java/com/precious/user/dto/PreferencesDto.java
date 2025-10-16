package com.precious.user.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public class PreferencesDto {

    public static boolean isValid(JsonNode node) {
        if (node == null || !node.isObject()) return false;

        // Проверяем корневые ключи
        if (!node.has("металлы") && !node.has("валюты")) return false;

        // Валидация металлов
        if (node.has("металлы")) {
            JsonNode metals = node.get("металлы");
            if (!metals.isObject()) return false;
            Iterator<String> metalNames = metals.fieldNames();
            while (metalNames.hasNext()) {
                String name = metalNames.next();
                if (!isValidMetalName(name)) return false;
                if (!isValidMetalRules(metals.get(name))) return false;
            }
        }

        // Валидация валют
        if (node.has("валюты")) {
            JsonNode currencies = node.get("валюты");
            if (!currencies.isObject()) return false;
            Iterator<String> currencyCodes = currencies.fieldNames();
            while (currencyCodes.hasNext()) {
                String code = currencyCodes.next();
                if (!isValidCurrencyCode(code)) return false;
                if (!isValidCurrencyRules(currencies.get(code))) return false;
            }
        }

        return true;
    }

    private static boolean isValidMetalName(String name) {
        return "золото".equals(name) || "серебро".equals(name) || "платина".equals(name);
    }

    private static boolean isValidCurrencyCode(String code) {
        return "USD".equals(code) || "EUR".equals(code) || "GBP".equals(code) || "JPY".equals(code);
    }

    private static boolean isValidMetalRules(JsonNode rules) {
        return isValidPriceRule(rules, "buyBelow") || isValidPriceRule(rules, "sellAbove");
    }

    private static boolean isValidCurrencyRules(JsonNode rules) {
        return isValidPriceRule(rules, "buyBelow") || isValidPriceRule(rules, "sellAbove");
    }

    private static boolean isValidPriceRule(JsonNode rules, String key) {
        if (!rules.has(key)) return false;
        JsonNode value = rules.get(key);
        return value.isNumber() && value.asDouble() > 0;
    }
}