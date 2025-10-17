package com.precious.metal.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class SberbankMetalClient {

    @Value("${app.sberbank.metal.url}")
    private String METAL_URL;

    // Пример структуры: <tr><td>Золото</td><td>6500.50</td><td>6300.20</td></tr>
    public Map<String, MetalPriceData> fetchCurrentPrices() throws IOException {
        Document doc = Jsoup.connect(METAL_URL)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get();

        Map<String, MetalPriceData> prices = new HashMap<>();

        // Находим таблицу — адаптируйте селектор под реальную страницу
        Elements rows = doc.select("table tr");

        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() < 3) continue;

            String name = cols.get(0).text().trim();
            if (name.isEmpty()) continue;

            try {
                BigDecimal buy = parsePrice(cols.get(1).text());
                BigDecimal sell = parsePrice(cols.get(2).text());
                prices.put(name, new MetalPriceData(buy, sell));
            } catch (Exception e) {
                // Логируем и пропускаем некорректные строки
                System.err.println("Failed to parse row: " + cols.text() + " | Error: " + e.getMessage());
            }
        }

        return prices;
    }

    private BigDecimal parsePrice(String text) {
        // Убираем всё кроме цифр и точки/запятой
        String clean = text.replaceAll("[^\\d,\\.]", "").replace(',', '.');
        return new BigDecimal(clean);
    }

    public static class MetalPriceData {
        private final BigDecimal buyPrice;
        private final BigDecimal sellPrice;

        public MetalPriceData(BigDecimal buyPrice, BigDecimal sellPrice) {
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }

        public BigDecimal getBuyPrice() { return buyPrice; }
        public BigDecimal getSellPrice() { return sellPrice; }
    }
}