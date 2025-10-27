package com.precious.shared.model;

public enum JsonKeys {
    NAME("name"),
    BUY_PRICE("buyPrice"),
    SELL_PRICE("sellPrice"),
    TIMESTAMP("timestamp"),
    BANK("bank");

    private final String key;

    JsonKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
