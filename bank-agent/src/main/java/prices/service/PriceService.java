package prices.service;

import com.precious.shared.model.Banks;
import com.precious.shared.model.Currency;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prices.agent.Agent;
import prices.agent.sber.CurrencySberAgent;
import prices.agent.sber.MetalSberAgent;
import prices.model.CurrencyPrice;
import prices.model.MetalPrice;
import prices.repository.PriceRepository;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PriceService<T> {

    private final Agent bgent;
    private final PriceRepository<T> priceRepository;

    public PriceService(Agent bgent, PriceRepository<T> priceRepository) {
        this.bgent = bgent;
        this.priceRepository = priceRepository;
    }

    @Transactional
    public void updatePrices(TypePrice typePrice) {
        Map<String, JSONObject> current = bgent.getPrices(typePrice);

        for (Map.Entry<String, JSONObject> entry : current.entrySet()) {
            String name = entry.getKey();
            JSONObject data = entry.getValue();

            BigDecimal buyPrice = parsePrice(data.get(CurrentPrice.BUY.name()).toString());
            BigDecimal sellPrice = parsePrice(data.get(CurrentPrice.SELL.name()).toString());

            switch (typePrice) {
                case SBER_METAL -> {
                    var latest = priceRepository.findLatestByName(name);
                    MetalPrice newPrice = new MetalPrice(name, buyPrice, sellPrice, Banks.SBER.name());
                    if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                        priceRepository.save(newPrice);
                    }
                }
                case SBER_CURRENCY -> {
                    var latest = priceRepository.findLatestByName(name);
                    CurrencyPrice newPrice = new CurrencyPrice(name, buyPrice, sellPrice, Banks.SBER.name());
                    if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                        priceRepository.save(newPrice);
                    }
                }
            }
        }
    }

    public JSONArray getPrices(TypePrice typePrice) {
        JSONArray array = new JSONArray();
        switch (typePrice) {
            case SBER_METAL -> {
                for (Metal metal : Metal.values()) {
                    array.add(getPrices(typePrice, metal.name()));
                }
            }
            case SBER_CURRENCY -> {
                for (Currency currency : Currency.values()) {
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
        return JsonUtils.getPriceToJson(name, null, null, null, null);
    }

    private BigDecimal parsePrice(String text) {
        String clean = text.replaceAll("[^\\d,\\.]", "").replace(',', '.');
        return new BigDecimal(clean);
    }
}