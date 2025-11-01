package prices.agent.sber.metal;

import prices.agent.AgentConfig;

public enum SberAgentMetalConfig implements AgentConfig {

    URL("https://www.sberbank.ru/retail/ru/quotes/metalbeznal?tab=online"),
    WEB_ELEMENT("//div[contains(@class, 'rfn-table-currency__iso') " +
            "and text()='%s']" +
            "/ancestor::div[contains(@class, 'rfn-table-row')]" +
            "//div[contains(@class, 'rfn-table-row__price_main')]" +
            "//div[contains(@class, 'rfn-table-row__col')]" +
            "[%s]"),
    INDEX_BUY("3"),
    INDEX_SELL("2");

    private final String config;

    SberAgentMetalConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }
}
