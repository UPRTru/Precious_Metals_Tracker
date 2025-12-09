package prices.agent.sber.currency;

import com.precious.shared.dto.Price;
import com.precious.shared.enums.Banks;
import com.precious.shared.enums.Currency;
import com.precious.shared.enums.CurrentPrice;
import net.minidev.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;
import prices.agent.Agent;
import prices.agent.AgentConfig;
import prices.agent.EnumAgentsConfig;
import prices.agent.WebDriverSupport;
import prices.builder.PriceBuilder;
import prices.model.CurrencyPrice;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component(CurrencySberAgent.AGENT_NAME)
public class CurrencySberAgent implements Agent {

    public static final String AGENT_NAME = "sber agent current";
    private static final int MAX_COUNT_CURRENCY = 5;

    private final AgentConfig agentConfig;
    private final WebDriverSupport webDriverSupport;

    public CurrencySberAgent() {
        agentConfig = EnumAgentsConfig.SBER_CURRENT.getAgentConfig();
        webDriverSupport = new WebDriverSupport();
    }

    @Override
    public HashMap<String, Price> getPrices() {
        webDriverSupport.createDriver();
        int iterations = getCountIterations(Currency.getCurrencyByBanks(Banks.SBER));
        int index = 0;
        HashMap<String, Price> result = new HashMap<>(Currency.values().length);
        while (iterations > 0) {
            List<Currency> currencies = new ArrayList<>(MAX_COUNT_CURRENCY);
            String[] currencyNames = new String[MAX_COUNT_CURRENCY];
            for (int i = 0; i < MAX_COUNT_CURRENCY; i++) {
                Currency currency = Currency.values()[index];
                currencyNames[i] = currency.name();
                currencies.add(currency);
                index++;
            }
            String url = String.format(agentConfig.getUrl(),
                    currencyNames[0],
                    currencyNames[1],
                    currencyNames[2],
                    currencyNames[3],
                    currencyNames[4]);
            webDriverSupport.goToPage(url);
            currencies.forEach(currency -> {
                result.put(currency.name(), new Price(Banks.SBER.name(), currency.getDisplayName(), getCurrencyPrice(currency, CurrentPrice.BUY), getCurrencyPrice(currency, CurrentPrice.SELL), Instant.now().toEpochMilli()));
            });
            iterations--;
        }
        webDriverSupport.closeDriver();
        return result;
    }

    private int getCountIterations(List<Currency> allCurrenciesByBank) {
        if (allCurrenciesByBank.isEmpty()) {
            return 0;
        } else if (allCurrenciesByBank.size() <= MAX_COUNT_CURRENCY) {
            return 1;
        } else {
            return allCurrenciesByBank.size() / MAX_COUNT_CURRENCY;
        }
    }

    private BigDecimal getCurrencyPrice(Currency currency, CurrentPrice currentPrice) {
        WebElement webElement = getWebElement(currency, currentPrice);
        return BigDecimal.valueOf(Double.valueOf(webElement.getText()));
    }

    private WebElement getWebElement(Currency currency, CurrentPrice currentPrice) {
        String index;
        switch (currentPrice) {
            case BUY:
                index = agentConfig.getIndexBuy();
                break;
            case SELL:
                index = agentConfig.getIndexSell();
                break;
            default:
                index = "";
        }

        return webDriverSupport.getWebDriver().until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(String.format(agentConfig.getWebElement(), currency.getDisplayName(), currency.name(), index))
                )
        );
    }
}