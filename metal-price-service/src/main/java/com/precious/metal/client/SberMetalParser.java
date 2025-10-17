package com.precious.metal.client;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SberMetalParser {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:/chrome-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            System.out.println("Загрузка страницы через Selenium...");
            driver.get("https://www.sberbank.ru/retail/ru/quotes/metalbeznal?tab=online");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class, 'rfn-table-currency__iso') and text()='Золото']/ancestor::div[contains(@class, 'rfn-table-row')]//div[contains(@class, 'rfn-table-row__price_main')]//div[contains(@class, 'rfn-table-row__col')][2]")
                    )
            );
            WebElement element2 = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class, 'rfn-table-currency__iso') and text()='Золото']/ancestor::div[contains(@class, 'rfn-table-row')]//div[contains(@class, 'rfn-table-row__price_main')]//div[contains(@class, 'rfn-table-row__col')][3]")
                    )
            );
            String text = element.getText();
            String text2 = element2.getText();

            System.out.println("Золото продать за: " + text);
            System.out.println("Золото купить за: " + text2);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // важно: закрывает браузер
        }
    }
}