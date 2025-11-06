package prices.utils;

import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.JsonKeys;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import prices.model.CurrencyPrice;
import prices.model.MetalPrice;
import prices.model.PriceInterface;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtils {

    public static JSONObject createJsonObject(String name) {
        return new JSONObject().appendField(JsonKeys.NAME.getKey(), name);
    }

    public static JSONObject appendPrice(JSONObject jsonObject, CurrentPrice currentPrice, String value) {
        jsonObject.appendField(currentPrice.name(), value);
        return jsonObject;
    }

    public static JSONObject getPriceToJson(String name, BigDecimal buyPrice, BigDecimal sellPrice, Long timestamp, String bank) {
        return new JSONObject()
                .appendField(JsonKeys.NAME.getKey(), name)
                .appendField(JsonKeys.BUY_PRICE.getKey(), buyPrice)
                .appendField(JsonKeys.SELL_PRICE.getKey(), sellPrice)
                .appendField(JsonKeys.TIMESTAMP.getKey(), timestamp)
                .appendField(JsonKeys.BANK.getKey(), bank);
    }

    public static JSONObject addCustomField(JSONObject jsonObject, String key, String value) {
        jsonObject.appendField(key, value);
        return jsonObject;
    }

    public static JSONArray mappingToJsonArray(List<? extends PriceInterface> objects) {
        return objects.stream()
                .map(PriceInterface::toJsonObject)
                .collect(Collectors.toCollection(JSONArray::new));
    }
}
