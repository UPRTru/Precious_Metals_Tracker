package com.precious.metal.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class SberMetalParser {

    public static void main(String[] args) {
        //xpath = //div[contains(@class, 'rfn-table-currency__iso') and text()='Золото']/ancestor::div[contains(@class, 'rfn-table-row')]//div[contains(@class, 'rfn-table-row__price_main')]//div[contains(@class, 'rfn-table-row__col')][2]/text()
        // Укажите путь к chromedriver.exe (Windows) или chromedriver (macOS/Linux)
        // Если он в PATH — можно не указывать
         System.setProperty("webdriver.chrome.driver", "C:/chrome-win64");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // запуск без GUI (можно убрать для отладки)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            System.out.println("Загрузка страницы через Selenium...");
            driver.get("https://www.sberbank.ru/retail/ru/quotes/metalbeznal?tab=online");

            // Ждём, пока таблица появится (можно использовать WebDriverWait для надёжности)
            Thread.sleep(5000); // простая задержка; лучше заменить на WebDriverWait

            String pageHtml = driver.getPageSource();
            Document doc = Jsoup.parse(pageHtml);

            // Ищем цену "Продать" для Золота
            Elements priceElements = doc.select(
                    "div.rfn-table-row:has(div.rfn-table-currency__iso:contains(Золото)) " +
                            "div.rfn-table-row__price_main div.rfn-table-row__col:nth-child(2)"
            );

            if (!priceElements.isEmpty()) {
                Element priceEl = priceElements.first();
                String rawText = priceEl.ownText().trim();

                String cleanText = rawText.replace("\u00A0", "") // убираем неразрывные пробелы
                        .replace(",", ".");

                System.out.println("Сырой текст: [" + rawText + "]");
                System.out.println("Очищенный: " + cleanText);

                double value = Double.parseDouble(cleanText);
                System.out.println("Число: " + value);
            } else {
                System.out.println("Элемент не найден. Возможно, структура изменилась.");
                // Для отладки: сохраните pageHtml в файл и проверьте
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // важно: закрывает браузер
        }
    }
}