package com.precious.metal.service;

import com.precious.metal.client.ByOrSell;
import com.precious.metal.client.SberMetalParser;
import com.precious.metal.model.MetalPrice;
import com.precious.metal.repository.MetalPriceRepository;
import com.precious.shared.model.Metal;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class MetalPriceService {

    private final SberMetalParser parser;
    private final MetalPriceRepository repository;

    public MetalPriceService(SberMetalParser parser, MetalPriceRepository repository) {
        this.parser = parser;
        this.repository = repository;
    }

    @Transactional
    public void fetchAndSaveIfChanged() {
        Map<Metal, JSONArray> current = parser.getPrices();
        for (Map.Entry<Metal, JSONArray> entry : current.entrySet()) {
            String metalName = entry.getKey().getDisplayName();
            JSONArray data = entry.getValue();

            BigDecimal buyPrice = null;
            BigDecimal sellPrice = null;

            for (Object o : data) {
                String value = ((JSONObject) o).toJSONString();
                if (value.contains(ByOrSell.BUY.name())) {
                    buyPrice = parsePrice(value);
                } else if (value.contains(ByOrSell.SELL.name())) {
                    sellPrice = parsePrice(value);
                }
            }

            var latest = repository.findLatestByMetalName(metalName);

            MetalPrice newPrice = new MetalPrice(metalName, buyPrice, sellPrice);

            if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                repository.save(newPrice);
            }
        }
    }

    public MetalPrice getLatestPrice(String metalName) {
        return repository.findLatestByMetalName(metalName)
                .orElseThrow(() -> new IllegalArgumentException("No price found for metal: " + metalName));
    }

    private BigDecimal parsePrice(String text) {
        String clean = text.replaceAll("[^\\d,\\.]", "").replace(',', '.');
        return new BigDecimal(clean);
    }
}