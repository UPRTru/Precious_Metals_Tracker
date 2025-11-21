package prices.agent.sber.metal;

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
import org.springframework.stereotype.Component;
import prices.agent.Agent;
import prices.agent.AgentConfig;
import prices.agent.EnumAgentsConfig;
import prices.utils.JsonUtils;

import java.time.Duration;
import java.util.HashMap;

@Component(MetalSberAgent.AGENT_NAME)
public class MetalSberAgent implements Agent {

    public static final String AGENT_NAME = "sber agent metal";

    private final ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait webDriver;
    private final AgentConfig agentConfig;

    public MetalSberAgent() {
        agentConfig = EnumAgentsConfig.SBER_METAL.getAgentConfig();
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
        goToPage(agentConfig.getUrl());
        HashMap<String, JSONObject> result = getMetalsPrices();
        closeDriver();
        return result;
    }

    private void createDriver() {
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    private void goToPage(String url) {
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
                index = agentConfig.getIndexBuy();
                break;
            case SELL:
                index = agentConfig.getIndexSell();
                break;
            default:
                index = "";
        }

        return webDriver.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(String.format(agentConfig.getWebElement(), metal.getDisplayName(), index))
                )
        );
    }
}