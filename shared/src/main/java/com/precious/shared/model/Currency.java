package com.precious.shared.model;

public enum Currency {
    USD("Доллар США"),
    EUR("Евро"),
    JPY("Японская иена"),
    CNY("Китайский юань"),
    AED("Дирхам ОАЭ");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Currency fromDisplayName(String name) {
        for (Currency c : values()) {
            if (c.displayName.equalsIgnoreCase(name)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown currency: " + name);
    }

}