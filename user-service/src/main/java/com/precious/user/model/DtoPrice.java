package com.precious.user.model;

import com.precious.shared.model.JsonKeys;
import net.minidev.json.JSONObject;

public class DtoPrice {

    private final String bank;
    private final String typePrice;
    private final String name;
    private final String currentPrice;
    private final String price;

    public DtoPrice(String bank, String typePrice, String name, String currentPrice, String price) {
        this.bank = bank;
        this.typePrice = typePrice;
        this.name = name;
        this.currentPrice = currentPrice;
        this.price = price;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .appendField(JsonKeys.BANK.getKey(), bank)
                .appendField(JsonKeys.TYPE_PRICE.getKey(), typePrice)
                .appendField(JsonKeys.NAME.getKey(), name)
                .appendField(JsonKeys.CURRENT_PRICE.getKey(), currentPrice)
                .appendField(JsonKeys.CustomFields.PRICE.getKey(), price);
    }
}
