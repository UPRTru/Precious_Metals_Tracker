package prices.agent.sber;

import com.precious.shared.model.Banks;
import com.precious.shared.model.Currency;
import com.precious.shared.model.CurrentPrice;
import net.minidev.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import prices.agent.Agent;
import prices.service.TypePrice;
import prices.utils.JsonUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class CurrencySberAgent implements Agent {

    private String url;
    private final ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait webDriver;
    private final int MAX_COUNT_CURRENCY = 5;

    public CurrencySberAgent() {
        options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", "C:/chrome-win64/chromedriver.exe");
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
    }

    @Override
    public HashMap<String, JSONObject> getPrices(TypePrice typePrice) {
        createDriver();
        List<Currency> allCurrenciesByBank;
        switch (typePrice) {
            case SBER_CURRENCY -> allCurrenciesByBank = Currency.getCurrencyByBanks(Banks.SBER);
            default -> allCurrenciesByBank = List.of();
        }
        int iterations = getCountIterations(allCurrenciesByBank);
        int index = 0;
        HashMap<String, JSONObject> result = new HashMap<>(Currency.values().length);
        while (iterations > 0) {
            List<Currency> currencies = new ArrayList<>(MAX_COUNT_CURRENCY);
            String[] currencyNames = new String[MAX_COUNT_CURRENCY];
            for (int i = 0; i < MAX_COUNT_CURRENCY; i++) {
                Currency currency = Currency.values()[index];
                currencyNames[i] = currency.name();
                currencies.add(currency);
                index++;
            }
            url = String.format(SberAgentConfig.CURRENCY_URL.getConfig(), currencyNames[0], currencyNames[1], currencyNames[2], currencyNames[3], currencyNames[4]);
            goToPage();
            result.putAll(getCurrenciesPrices(currencies));
            iterations--;
        }
        closeDriver();
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

    private void createDriver() {
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    private void goToPage() {
        driver.get(url);
        webDriver = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void closeDriver() {
        driver.quit();
    }

    private HashMap<String, JSONObject> getCurrenciesPrices(List<Currency> currencies) {
        HashMap<String, JSONObject> result = new HashMap<>();
        for (Currency currency : currencies) {
            result.putAll(getCurrencyPrices(currency));
        }
        return result;
    }

    private HashMap<String, JSONObject> getCurrencyPrices(Currency currency) {
        JSONObject jsonObject = JsonUtils.createJsonObject(currency.name());
        getJsonCurrencyPrice(jsonObject, currency, CurrentPrice.BUY);
        getJsonCurrencyPrice(jsonObject, currency, CurrentPrice.SELL);
        HashMap<String, JSONObject> result = new HashMap<>();
        result.put(currency.name(), jsonObject);
        return result;
    }

    private void getJsonCurrencyPrice(JSONObject jsonObject, Currency currency, CurrentPrice currentPrice) {
        WebElement webElement = getWebElement(currency, currentPrice);
        JsonUtils.appendPrice(jsonObject, currentPrice, webElement.getText());
    }

    private WebElement getWebElement(Currency currency, CurrentPrice currentPrice) {
        String index;
        switch (currentPrice) {
            case BUY:
                index = SberAgentConfig.CURRENCY_INDEX_BUY.getConfig();
                break;
            case SELL:
                index = SberAgentConfig.CURRENCY_INDEX_SELL.getConfig();
                break;
            default:
                index = "";
        }

        return webDriver.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(String.format(SberAgentConfig.CURRENCY_WEB_ELEMENT.getConfig(), currency.getDisplayName(), currency.name(), index))
                )
        );
    }
}