package prices.service;

import com.precious.shared.model.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prices.agent.Agent;
import prices.agent.AgentConfig;
import prices.model.CurrencyPrice;
import prices.model.MetalPrice;
import prices.repository.CurrencyPriceRepository;
import prices.repository.MetalPriceRepository;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PriceService<T extends AgentConfig> {

    private final MetalPriceRepository metalPriceRepository;
    private final CurrencyPriceRepository currencyPriceRepository;

    public PriceService(MetalPriceRepository metalPriceRepository,
                        CurrencyPriceRepository currencyPriceRepository) {
        this.metalPriceRepository = metalPriceRepository;
        this.currencyPriceRepository = currencyPriceRepository;
    }

    @Transactional
    public void updatePrices(TypePrice typePrice, Agent agent) {
        Map<String, JSONObject> current = agent.getPrices();

        for (Map.Entry<String, JSONObject> entry : current.entrySet()) {
            String name = entry.getKey();
            JSONObject data = entry.getValue();

            BigDecimal buyPrice = parsePrice(data.get(CurrentPrice.BUY.name()).toString());
            BigDecimal sellPrice = parsePrice(data.get(CurrentPrice.SELL.name()).toString());

            switch (typePrice) {
                case METAL -> {
                    var latest = metalPriceRepository.findLatestByName(name);
                    MetalPrice newPrice = new MetalPrice(name, buyPrice, sellPrice, Banks.SBER.name());
                    if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                        metalPriceRepository.save(newPrice);
                    }
                }
                case CURRENCY -> {
                    var latest = currencyPriceRepository.findLatestByName(name);
                    CurrencyPrice newPrice = new CurrencyPrice(name, buyPrice, sellPrice, Banks.SBER.name());
                    if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                        currencyPriceRepository.save(newPrice);
                    }
                }
            }
        }
    }

    public JSONArray getPrices(TypePrice typePrice) {
        JSONArray array = new JSONArray();
        switch (typePrice) {
            case METAL -> {
                for (Metal metal : Metal.values()) {
                    array.add(getPrices(typePrice, metal.name()));
                }
            }
            case CURRENCY -> {
                for (Currency currency : Currency.values()) {
                    array.add(getPrices(typePrice, currency.name()));
                }
            }
        }
        return array;
    }

    public JSONObject getPrices(TypePrice typePrice, String name) {
        switch (typePrice) {
            case METAL -> {
                var latest = metalPriceRepository.findLatestByName(name);
                if (!latest.isEmpty()) {
                    return latest.get().toJsonObject();
                }
            }
            case CURRENCY -> {
                var latest = currencyPriceRepository.findLatestByName(name);
                if (!latest.isEmpty()) {
                    return latest.get().toJsonObject();
                }
            }
        }
        return JsonUtils.getPriceToJson(name, null, null, null, null);
    }

    private BigDecimal parsePrice(String text) {
        String clean = text.replaceAll("[^\\d,\\.]", "").replace(',', '.');
        return new BigDecimal(clean);
    }
}