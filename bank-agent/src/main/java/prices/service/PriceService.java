package prices.service;

import com.precious.shared.model.Banks;
import prices.agent.Agent;
import prices.agent.sber.CurrencySberAgent;
import prices.agent.sber.MetalSberAgent;
import com.precious.shared.model.Currency;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
import prices.model.CurrencyPrice;
import prices.model.MetalPrice;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prices.repository.CurrencyPriceRepository;
import prices.repository.MetalPriceRepository;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PriceService {

    private Agent dgent;
    private final MetalPriceRepository metalPriceRepository;
    private final CurrencyPriceRepository currencyPriceRepository;

    public PriceService(MetalSberAgent dgent, MetalPriceRepository metalPriceRepository, CurrencyPriceRepository currencyPriceRepository) {
        this.dgent = dgent;
        this.metalPriceRepository = metalPriceRepository;
        this.currencyPriceRepository = currencyPriceRepository;
    }

    @Transactional
    public void updatePrices(TypePrice typePrice) {
        if (typePrice.equals(TypePrice.SBER_METAL)) {
            dgent = new MetalSberAgent();
        } else if (typePrice.equals(TypePrice.SBER_CURRENCY)) {
            dgent = new CurrencySberAgent();
        }
        Map<String, JSONObject> current = dgent.getPrices(typePrice);

        for (Map.Entry<String, JSONObject> entry : current.entrySet()) {
            String name = entry.getKey();
            JSONObject data = entry.getValue();

            BigDecimal buyPrice = parsePrice(data.get(CurrentPrice.BUY.name()).toString());
            BigDecimal sellPrice = parsePrice(data.get(CurrentPrice.SELL.name()).toString());

            switch (typePrice) {
                case SBER_METAL -> {
                    var latest = metalPriceRepository.findLatestByMetalName(name);
                    MetalPrice newPrice = new MetalPrice(name, buyPrice, sellPrice, Banks.SBER.name());
                    if (latest.isEmpty() || !latest.get().equals(newPrice)) {
                        metalPriceRepository.save(newPrice);
                    }
                }
                case SBER_CURRENCY -> {
                    var latest = currencyPriceRepository.findLatestByCurrencyName(name);
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
        return JsonUtils.getPriceToJson(name, null, null, null, null);
    }

    private BigDecimal parsePrice(String text) {
        String clean = text.replaceAll("[^\\d,\\.]", "").replace(',', '.');
        return new BigDecimal(clean);
    }
}