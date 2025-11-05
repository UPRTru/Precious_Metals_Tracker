package com.precious.shared.model;

public enum JsonKeys {
    CURRENT_PRICE("currentPrice"),
    TYPE_PRICE("typePrice"),
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

    public enum CustomFields {
        WEIGHT("weight"),
        PRICE("price");

        private final String key;

        CustomFields(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
