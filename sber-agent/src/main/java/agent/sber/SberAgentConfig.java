package agent.sber;

public enum SberAgentConfig {
    CURRENCY_URL("https://www.sberbank.ru/ru/quotes/currencies?tab=sbol" +
            "&currency=%s" +
            "&currency=%s" +
            "&currency=%s" +
            "&currency=%s" +
            "&currency=%s" +
            "&package=ERNP-2"),
    METAL_URL("https://www.sberbank.ru/retail/ru/quotes/metalbeznal?tab=online"),

    CURRENCY_WEB_ELEMENT("//div[starts-with(@class, 'TabContainer') and not(contains(substring-after(@class, 'TabContainer'), ' '))]"
            + "//div[contains(@class, 'rates-form-new-table-row')]"
            + "[contains(., '%s') or contains(., '%s')]"
            + "//div[contains(@class, 'rates-form-new-table-row__col-wrap')]"
            + "//div[%s]"
            + "//div[contains(@class, 'dk-sbol-text') and contains(text(), '₽')]"),
    METAL_WEB_ELEMENT("//div[contains(@class, 'rfn-table-currency__iso') " +
            "and text()='%s']" +
            "/ancestor::div[contains(@class, 'rfn-table-row')]" +
            "//div[contains(@class, 'rfn-table-row__price_main')]" +
            "//div[contains(@class, 'rfn-table-row__col')]" +
            "[%s]"),

    CURRENCY_INDEX_BUY("1"),
    CURRENCY_INDEX_SELL("2"),

    METAL_INDEX_BUY("3"),
    METAL_INDEX_SELL("2");

    private final String config;

    SberAgentConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }


}
