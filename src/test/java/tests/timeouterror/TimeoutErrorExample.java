package tests.timeouterror;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import org.junit.jupiter.api.Assertions;

public class TimeoutErrorExample {

    public static void main(String[] args) {

        /**
         * TimeoutError — подкласс PlaywrightException, возникающий при превышении времени выполнения операций
         * (например, Locator.click(), Page.waitForSelector(), Browser.launch()).
         * <br>
         * Особенности
         * 1. Когда возникает:
         * - Операция не завершилась за заданный таймаут.
         * - Типичные сценарии: ожидание элемента, загрузка страницы, запуск браузера.
         * 2. Как обрабатывать:
         * - Перехватывать через try-catch.
         * - Увеличивать таймаут через setTimeout() в опциях.
         * <br>
         * Типичные операции с таймаутами
         * Locator.click() - setTimeout()	30 сек
         * Page.waitForSelector() - setTimeout()	30 сек
         * Page.navigate() - setTimeout()	30 сек
         * BrowserType.launch() - setTimeout()	30 сек
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://example.com");


            // ===================== Пример обработки TimeoutError =====================
            // Попытка кликнуть по несуществующему элементу с малым таймаутом
            try {
                page.locator("text=NonExistentElement").click(
                        new Locator.ClickOptions().setTimeout(100) // 100 мс
                );
                Assertions.fail("Expected TimeoutError was not thrown");
            } catch (TimeoutError e) {
                System.out.println("Caught TimeoutError: " + e.getMessage());
                // Дополнительные действия: логирование, повтор запроса и т.д.
            }


            // ===================== Увеличить таймаут: =====================
            page.locator("button").click(new Locator.ClickOptions().setTimeout(30_000)); // 30 сек


            // ===================== Использовать умные ожидания: =====================
            // Ждать появления элемента до клика
            page.locator("button").waitFor(new Locator.WaitForOptions().setTimeout(10_000));
            page.locator("button").click();


            // ===================== Проверять наличие элемента: =====================
            if (page.locator("text=Submit").isVisible()) {
                page.locator("text=Submit").click();
            }

        }
    }
}
