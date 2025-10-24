package prices.agent.sber;

import prices.agent.Agent;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
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
import prices.utils.JsonUtils;

import java.time.Duration;
import java.util.HashMap;

@Component
public class MetalSberAgent implements Agent {

    private final String url;
    private final ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait webDriver;

    public MetalSberAgent() {
        this.url = SberAgentConfig.METAL_URL.getConfig();
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
    public HashMap<String, JSONObject> getPrices() {
        createDriver();
        goToPage();
        HashMap<String, JSONObject> result = getMetalsPrices();
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

    private HashMap<String, JSONObject> getMetalsPrices() {
        HashMap<String, JSONObject> result = new HashMap<>();
        for (Metal metal : Metal.values()) {
            result.putAll(getMetalPrices(metal));
        }
        return result;
    }

    private HashMap<String, JSONObject> getMetalPrices(Metal metal) {
        JSONObject jsonObject = JsonUtils.createJsonObject(metal.name());
        getJsonMetalPrice(jsonObject, metal, CurrentPrice.BUY);
        getJsonMetalPrice(jsonObject, metal, CurrentPrice.SELL);
        HashMap<String, JSONObject> result = new HashMap<>();
        result.put(metal.name(), jsonObject);
        return result;
    }

    private void getJsonMetalPrice(JSONObject jsonObject, Metal metal, CurrentPrice currentPrice) {
        WebElement webElement = getWebElement(metal, currentPrice);
        JsonUtils.appendPrice(jsonObject, currentPrice, webElement.getText());
    }

    private WebElement getWebElement(Metal metal, CurrentPrice currentPrice) {
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