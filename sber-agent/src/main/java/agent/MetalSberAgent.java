package agent;

import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
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
public class MetalSberAgent implements SberAgent {

    @Value("C:/chrome-win64/chromedriver.exe")
    private String CHROME_DRIVER_PATH;
    private String url;
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait webDriver;

    public MetalSberAgent() {
        this.url = SberAgentConfig.METAL_URL.getConfig();
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
    public HashMap<Metal, JSONArray> getPrices() {
        createDriver();
        goToPage();
        HashMap<Metal, JSONArray> result = getMetalsPrices();
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

    private HashMap<Metal, JSONArray> getMetalsPrices() {
        HashMap<Metal, JSONArray> result = new HashMap<>();
        for (Metal metal : Metal.values()) {
            HashMap<Metal, JSONArray> metalPrices = getMetalPrices(metal);
            result.putAll(metalPrices);
        }
        return result;
    }

    private HashMap<Metal, JSONArray> getMetalPrices(Metal metal) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(getJsonMetalPrice(metal, webDriver, CurrentPrice.BUY));
        jsonArray.add(getJsonMetalPrice(metal, webDriver, CurrentPrice.SELL));
        HashMap<Metal, JSONArray> result = new HashMap<>();
        result.put(metal, jsonArray);
        return result;
    }

    private JSONObject getJsonMetalPrice(Metal metal, WebDriverWait webDriver, CurrentPrice currentPrice) {
        WebElement webElement = getWebElement(metal, webDriver, currentPrice);
        Map<String, String> map = new HashMap<>();
        map.put(currentPrice.name(), webElement.getText());
        return new JSONObject(map);
    }

    private WebElement getWebElement(Metal metal, WebDriverWait webDriver, CurrentPrice currentPrice) {
        String index;
        switch (currentPrice) {
            case BUY:
                index = SberAgentConfig.METAL_INDEX_BUY.getConfig();
                break;
            case SELL:
                index = SberAgentConfig.METAL_INDEX_SELL.getConfig();
                break;
            default:
                index = "";
        }

        return webDriver.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(String.format(SberAgentConfig.METAL_WEB_ELEMENT.getConfig(), metal.getDisplayName(), index))
                )
        );
    }
}