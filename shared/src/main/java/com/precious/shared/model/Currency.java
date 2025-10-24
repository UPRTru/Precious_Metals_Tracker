package com.precious.shared.model;

public enum Currency {
    USD("Доллар США"),
    EUR("Евро"),
    JPY("Японская иена"),
    CNY("Китайский юань"),
    AED("Дирхам ОАЭ"),
    TRY("Турецкая лира"),
    BYN("Белорусский рубль"),
    KZT("Казахстанский тенге"),
    GBP("Фунт стерлингов Соединенного Королевства"),
    PLN("Польский злотый"),
    SGD("Сингапурский доллар"),
    THB("Таиландский бат"),
    SEK("Шведская крона"),
    NOK("Норвежская крона"),
    CHF("Швейцарский франк"),
    CAD("Канадский доллар"),
    DKK("Датская крона"),
    HKD("Гонконгский доллар"),
    CZK("Чешская крона"),
    AUD("Австралийский доллар"),
    KRW("Вона Республики Корея");

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