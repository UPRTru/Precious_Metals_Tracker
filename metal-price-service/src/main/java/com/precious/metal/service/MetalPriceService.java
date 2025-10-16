package com.precious.metal.service;

import com.precious.metal.client.SberbankMetalClient;
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

    public double getCurrentBuyPrice(Metal metal) {
        // Попытка получить из Сбербанка, иначе fallback
        try {
            return sberbankClient.getCurrentBuyPrice(metal.getDisplayName())
                    .block(Duration.ofSeconds(3));
        } catch (Exception e) {
            log.warn("Сбербанк API недоступен, используем эмуляцию", e);
            return getFallbackPrice(metal);
        }
    }

    private double getFallbackPrice(Metal metal) {
        return switch (metal) {
            case GOLD -> 6500 + random.nextGaussian() * 100;
            case SILVER -> 85 + random.nextGaussian() * 5;
            case PLATINUM -> 2200 + random.nextGaussian() * 50;
        };
    }

    // Эмуляция текущих цен (в рублях за грамм)
//    public double getCurrentBuyPrice(Metal metal) {
//        return switch (metal) {
//            case GOLD -> 6500 + random.nextGaussian() * 100;
//            case SILVER -> 85 + random.nextGaussian() * 5;
//            case PLATINUM -> 2200 + random.nextGaussian() * 50;
//        };
//    }

    public double getCurrentSellPrice(Metal metal) {
        return getCurrentBuyPrice(metal) * 0.97; // банк покупает дешевле
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