package prices.builder;

import com.precious.shared.dto.Price;
import prices.model.CurrencyPrice;
import prices.model.MetalPrice;

public class PriceBuilder {

    public static Price buildPrice(CurrencyPrice currencyPrice) {
        return new Price(currencyPrice.getBank(),
                currencyPrice.getName(),
                currencyPrice.getBuyPrice(),
                currencyPrice.getSellPrice(),
                currencyPrice.getTimestamp());
    }

    public static Price buildPrice(MetalPrice metalPrice) {
        return new Price(metalPrice.getBank(),
                metalPrice.getName(),
                metalPrice.getBuyPrice(),
                metalPrice.getSellPrice(),
                metalPrice.getTimestamp());
    }
}
