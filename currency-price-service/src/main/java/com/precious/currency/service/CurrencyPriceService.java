package com.precious.currency.service;

import com.precious.currency.client.SberbankCurrencyClient;
import com.precious.shared.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class CurrencyPriceService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyPriceService.class);
    private final SberbankCurrencyClient sberbankClient;
    private final Random random;

    public CurrencyPriceService(SberbankCurrencyClient sberbankClient) {
        this.random = new Random(42);
        this.sberbankClient = sberbankClient;
    }

    public double getCurrentBuyPrice(Currency currency) {
        // Попытка получить из Сбербанка, иначе fallback
        try {
            return sberbankClient.getCurrentBuyPrice(currency.name())
                    .block(Duration.ofSeconds(3));
        } catch (Exception e) {
            log.warn("Сбербанк API недоступен, используем эмуляцию", e);
            return getFallbackPrice(currency);
        }
    }

    private double getFallbackPrice(Currency currency) {
        return switch (currency) {
            case USD -> 93.5;
            case EUR -> 101.0;
            case GBP -> 118.0;
            case JPY -> 0.62;
        };
    }

    public double getCurrentSellPrice(Currency currency) {
        return getCurrentBuyPrice(currency) * 0.995; // небольшая маржа
    }

    // Эмуляция истории за последние 30 дней
    public Map<LocalDate, Double> getHistory(Currency currency, LocalDate from, LocalDate to) {
        Map<LocalDate, Double> history = new HashMap<>();
        LocalDate current = from;
        double basePrice = switch (currency) {
            case USD -> 93.0;
            case EUR -> 100.0;
            case GBP -> 117.0;
            case JPY -> 0.61;
        };

        while (!current.isAfter(to)) {
            double variation = (random.nextDouble() - 0.5) * 4;
            if (currency == Currency.JPY) variation /= 100;
            history.put(current, basePrice + variation);
            current = current.plusDays(1);
        }
        return history;
    }
}