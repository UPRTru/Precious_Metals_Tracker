package com.precious.metal.service;

import com.precious.metal.client.SberbankMetalClient;
import com.precious.metal.model.MetalPrice;
import com.precious.metal.repository.MetalPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
public class MetalPriceService {

    private final SberbankMetalClient client;
    private final MetalPriceRepository repository;

    public MetalPriceService(SberbankMetalClient client, MetalPriceRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    @Transactional
    public void fetchAndSaveIfChanged() {
        try {
            Map<String, SberbankMetalClient.MetalPriceData> current = client.fetchCurrentPrices();

            for (Map.Entry<String, SberbankMetalClient.MetalPriceData> entry : current.entrySet()) {
                String metalName = entry.getKey();
                var data = entry.getValue();

                var latest = repository.findLatestByMetalName(metalName);

                MetalPrice newPrice = new MetalPrice(metalName, data.getBuyPrice(), data.getSellPrice());

                if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                    repository.save(newPrice);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch metal prices from Sberbank", e);
        }
    }

    public MetalPrice getLatestPrice(String metalName) {
        return repository.findLatestByMetalName(metalName)
                .orElseThrow(() -> new IllegalArgumentException("No price found for metal: " + metalName));
    }
}