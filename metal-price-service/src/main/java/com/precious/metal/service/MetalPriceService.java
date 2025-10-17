package com.precious.metal.service;

import com.precious.metal.client.SberbankMetalClient;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MetalPriceService {
    private static final Logger log = LoggerFactory.getLogger(MetalPriceService.class);
    private final SberbankMetalClient sberbankClient;
    private final Random random;

    public MetalPriceService(SberbankMetalClient sberbankClient) {
        this.random = new Random(42);
        this.sberbankClient = sberbankClient;
    }

    public double getCurrentPrice(Metal metal, CurrentPrice currentPrice) {
        // Попытка получить из Сбербанка, иначе fallback
        try {
            double result = sberbankClient.getCurrentBuyPrice(metal.getDisplayName(), currentPrice)
                    .block(Duration.ofSeconds(3));
            if (result != 0.0) {
                return result;
            }
        } catch (Exception e) {
            log.warn("Сбербанк API недоступен", e);
        }
        throw new RuntimeException("Не удалось получить цену металла"); //заменить на свою ошибку
    }

    // Эмуляция истории за последние 30 дней
    public Map<LocalDate, Double> getHistory(Metal metal, LocalDate from, LocalDate to) {
        Map<LocalDate, Double> history = new HashMap<>();
        LocalDate current = from;
        double basePrice = switch (metal) {
            case GOLD -> 6400;
            case SILVER -> 80;
            case PLATINUM -> 2100;
        };

        while (!current.isAfter(to)) {
            double variation = (random.nextDouble() - 0.5) * 200; // ±100
            if (metal == Metal.SILVER) variation /= 10;
            history.put(current, basePrice + variation);
            current = current.plusDays(1);
        }
        return history;
    }
}