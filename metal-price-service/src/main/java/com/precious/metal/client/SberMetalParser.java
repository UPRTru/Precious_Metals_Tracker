package com.precious.metal.client;

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
public class SberMetalParser {

    @Value("https://www.sberbank.ru/retail/ru/quotes/metalbeznal?tab=online")
    private String METAL_URL;
    @Value("C:/chrome-win64/chromedriver.exe")
    private String CHROME_DRIVER_PATH;
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait webDriver;

    public SberMetalParser() {
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

    public HashMap<Metal, JSONArray> getPrices() {
        createDriver();
        goToPage();
        HashMap<Metal, JSONArray> result = getMetalsPrices();
        closeDriver();
        return result;
    }

    public HashMap<Metal, JSONArray> getPrices(Metal metal) {
        createDriver();
        goToPage();
        HashMap<Metal, JSONArray> result = getMetalPrices(metal);
        closeDriver();
        return result;
    }

    private void createDriver() {
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    private void goToPage() {
        driver.get(METAL_URL);
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
        jsonArray.add(getJsonMetalPrice(metal, webDriver, ByOrSell.BUY));
        jsonArray.add(getJsonMetalPrice(metal, webDriver, ByOrSell.SELL));
        HashMap<Metal, JSONArray> result = new HashMap<>();
        result.put(metal, jsonArray);
        return result;
    }

    private JSONObject getJsonMetalPrice(Metal metal, WebDriverWait webDriver, ByOrSell byOrSell) {
        WebElement webElement = getWebElement(metal, webDriver, byOrSell);
        Map<String, String> map = new HashMap<>();
        map.put(byOrSell.name(), webElement.getText());
        return new JSONObject(map);
    }

    private WebElement getWebElement(Metal metal, WebDriverWait webDriver, ByOrSell byOrSell) {
        return webDriver.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'rfn-table-currency__iso') and text()='"
                                + metal.getDisplayName()
                                + "']/ancestor::div[contains(@class, 'rfn-table-row')]" +
                                "//div[contains(@class, 'rfn-table-row__price_main')]" +
                                "//div[contains(@class, 'rfn-table-row__col')]["
                                + byOrSell.getIndex() + "]")
                )
        );
    }
}