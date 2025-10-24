package utils;

import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.JsonKeys;
import net.minidev.json.JSONObject;

import java.math.BigDecimal;

public class JsonUtils {

    public static JSONObject createJsonObject(String name) {
        return new JSONObject().appendField(JsonKeys.NAME.getKey(), name);
    }

    public static JSONObject appendPrice(JSONObject jsonObject, CurrentPrice currentPrice, String value) {
        jsonObject.appendField(currentPrice.name(), value);
        return jsonObject;
    }

    public static JSONObject getPriceToJson(String name, BigDecimal buyPrice, BigDecimal sellPrice, Long timestamp) {
        return new JSONObject()
                .appendField(JsonKeys.NAME.getKey(), name)
                .appendField(JsonKeys.BUY_PRICE.getKey(), buyPrice)
                .appendField(JsonKeys.SELL_PRICE.getKey(), sellPrice)
                .appendField(JsonKeys.TIMESTAMP.getKey(), timestamp);
    }
}
