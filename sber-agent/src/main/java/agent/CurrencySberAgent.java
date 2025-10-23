package agent;

import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Currency;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencySberAgent implements SberAgent {

    @Value("C:/chrome-win64/chromedriver.exe")
    private String CHROME_DRIVER_PATH;
    private String url;
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait webDriver;

    public CurrencySberAgent() {
        this.url = SberAgentConfig.CURRENCY_URL.getConfig();
        options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
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
    public HashMap<Currency, JSONArray> getPrices() {
        createDriver();
        goToPage();
        HashMap<Currency, JSONArray> result = getCurrencysPrices();
        closeDriver();
        return result;
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

    private HashMap<Currency, JSONArray> getCurrencysPrices() {
        HashMap<Currency, JSONArray> result = new HashMap<>();
        for (Currency currency : Currency.values()) {
            HashMap<Currency, JSONArray> CurrencyPrices = getCurrencyPrices(currency);
            result.putAll(CurrencyPrices);
        }
        return result;
    }

    private HashMap<Currency, JSONArray> getCurrencyPrices(Currency currency) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(getJsonCurrencyPrice(currency, webDriver, CurrentPrice.BUY));
        jsonArray.add(getJsonCurrencyPrice(currency, webDriver, CurrentPrice.SELL));
        HashMap<Currency, JSONArray> result = new HashMap<>();
        result.put(currency, jsonArray);
        return result;
    }

    private JSONObject getJsonCurrencyPrice(Currency currency, WebDriverWait webDriver, CurrentPrice currentPrice) {
        WebElement webElement = getWebElement(currency, webDriver, currentPrice);
        Map<String, String> map = new HashMap<>();
        map.put(currentPrice.name(), webElement.getText());
        return new JSONObject(map);
    }

    private WebElement getWebElement(Currency currency, WebDriverWait webDriver, CurrentPrice currentPrice) {
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
                        By.xpath(String.format(SberAgentConfig.METAL_WEB_ELEMENT.getConfig(), currency.getDisplayName(), currency.name(), index))
                )
        );
    }
}