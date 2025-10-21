package com.precious.metal.client;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.precious.shared.model.Metal;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SberMetalParser {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:/chrome-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=800,600");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            System.out.println("Загрузка страницы через Selenium...");
            driver.get("https://www.sberbank.ru/retail/ru/quotes/metalbeznal?tab=online");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            HashMap<Metal, JSONArray> prices = getMetalPrices(wait);

            System.out.println("-----------------------------------------------------");
            System.out.println("Курсы металлов:");

            for (Metal metal : prices.keySet()) {
                System.out.println(metal.name() + " продать за: " + prices.get(metal).get(0));
                System.out.println(metal.name() + " купить за: " + prices.get(metal).get(1));
                System.out.println("-----------------------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // важно: закрывает браузер
        }
    }

    public static HashMap<Metal, JSONArray> getMetalPrices(WebDriverWait webDriver) {
        HashMap<Metal, JSONArray> result = new HashMap<>();
        for (Metal metal : Metal.values()) {
            HashMap<Metal, JSONArray> metalPrices = getMetalPrices(metal, webDriver);
            result.putAll(metalPrices);
        }
        return result;
    }

    public static HashMap<Metal, JSONArray> getMetalPrices(Metal metal, WebDriverWait webDriver) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(getJsonMetalPrice(metal, webDriver, ByOrSell.BUY));
        jsonArray.add(getJsonMetalPrice(metal, webDriver, ByOrSell.SELL));

        HashMap<Metal, JSONArray> result = new HashMap<>();
        result.put(metal, jsonArray);
        return result;
    }

    private static JSONObject getJsonMetalPrice(Metal metal, WebDriverWait webDriver, ByOrSell byOrSell) {
        WebElement webElement = getWebElement(metal, webDriver, byOrSell);
        Map<String, String> map = new HashMap<>();
        map.put(byOrSell.name(), webElement.getText());
        return new JSONObject(map);
    }

    private static WebElement getWebElement(Metal metal, WebDriverWait webDriver, ByOrSell byOrSell) {
        return webDriver.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'rfn-table-currency__iso') and text()='" + metal.name() + "']/ancestor::div[contains(@class, 'rfn-table-row')]//div[contains(@class, 'rfn-table-row__price_main')]//div[contains(@class, 'rfn-table-row__col')][" + byOrSell.getIndex() + "]")
                )
        );
    }
}