package service;

import agent.Agent;
import agent.sber.MetalSberAgent;
import com.precious.shared.model.Currency;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
import model.CurrencyPrice;
import model.MetalPrice;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CurrencyPriceRepository;
import repository.MetalPriceRepository;
import utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PriceService {

    private final Agent dgent;
    private final MetalPriceRepository metalPriceRepository;
    private final CurrencyPriceRepository currencyPriceRepository;

    public PriceService(MetalSberAgent dgent, MetalPriceRepository metalPriceRepository, CurrencyPriceRepository currencyPriceRepository) {
        this.dgent = dgent;
        this.metalPriceRepository = metalPriceRepository;
        this.currencyPriceRepository = currencyPriceRepository;
    }

    @Transactional
    public void updatePrices(TypePrice typePrice) {
        Map<String, JSONObject> current = dgent.getPrices();

        for (Map.Entry<String, JSONObject> entry : current.entrySet()) {
            String name = entry.getKey();
            JSONObject data = entry.getValue();

            BigDecimal buyPrice = parsePrice(data.get(CurrentPrice.BUY.name()).toString());
            BigDecimal sellPrice = parsePrice(data.get(CurrentPrice.SELL.name()).toString());

            switch (typePrice) {
                case SBER_METAL -> {
                    var latest = metalPriceRepository.findLatestByMetalName(name);
                    MetalPrice newPrice = new MetalPrice(name, buyPrice, sellPrice);
                    if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                        metalPriceRepository.save(newPrice);
                    }
                }
                case SBER_CURRENCY -> {
                    var latest = currencyPriceRepository.findLatestByCurrencyName(name);
                    CurrencyPrice newPrice = new CurrencyPrice(name, buyPrice, sellPrice);
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
            case SBER_METAL -> {
                for (Metal metal: Metal.values()) {
                    array.add(getPrices(typePrice, metal.name()));
                }
            }
            case SBER_CURRENCY -> {
                for (Currency currency: Currency.values()) {
                    array.add(getPrices(typePrice, currency.name()));
                }
            }
        }
        return array;
    }

    public JSONObject getPrices(TypePrice typePrice, String name) {
        switch (typePrice) {
            case SBER_METAL -> {
                var latest = metalPriceRepository.findLatestByMetalName(name);
                if (!latest.isEmpty()) {
                    return latest.get().toJsonObject();
                }
            }
            case SBER_CURRENCY -> {
                var latest = currencyPriceRepository.findLatestByCurrencyName(name);
                if (!latest.isEmpty()) {
                    return latest.get().toJsonObject();
                }
            }
        }
        return JsonUtils.getPriceToJson(name, null, null, null);
    }

    private BigDecimal parsePrice(String text) {
        String clean = text.replaceAll("[^\\d,\\.]", "").replace(',', '.');
        return new BigDecimal(clean);
    }
}