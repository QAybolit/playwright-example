package tests.exceptions;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WebErrorExample {

    /**
     * WebError представляет необработанные исключения JavaScript, возникающие на странице.
     * Обрабатывается через событие BrowserContext.onWebError.
     * <br>
     * Когда возникает:
     * - При необработанных ошибках JavaScript (throw new Error())
     * - При синтаксических ошибках в скриптах
     * - При сбоях загрузки ресурсов (только если не перехвачены try/catch)
     * <br>
     * Ошибки, которые НЕ перехватываются:
     * - CORS-ошибки
     * - Ошибки загрузки изображений/CSS
     * - Сетевые ошибки (используйте page.onRequestFailed)
     * <br>
     * Отличие от других ошибок:
     * - WebError: Необработанные исключения JS
     * - Page.onPageError: Альтернативный обработчик (только для конкретной страницы)
     * - ConsoleMessage: Сообщения консоли (включая console.error)
     * <br>
     * Методы:
     * - error()	Возвращает текст ошибки
     * - page()	Возвращает страницу, где произошла ошибка
     */

    // ===================== Полный пример тестового класса =====================
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static String capturedError;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();

        // 1. Регистрация обработчика ошибок
        // Обработчик срабатывает для всех страниц в контексте
        context.onWebError(webError -> {
            capturedError = webError.error();
            System.out.println("Captured WebError:");
            System.out.println(capturedError);

            // Проверка связанной страницы
            Page errorPage = webError.page();
            if (errorPage != null) {
                System.out.println("Error occurred on: " + errorPage.url());
            }
        });

        page = context.newPage();
    }

    @Test
    void testUnhandledError() {
        // 2. Навигация на страницу с ошибкой
        page.navigate("data:text/html,"
                + "<script>"
                + "  setTimeout(() => { throw new Error('Test Error') }, 100);"
                + "</script>");

        // 3. Ожидание ошибки
        page.waitForTimeout(200); // Даем время для возникновения ошибки

        // 4. Проверка перехвата
        Assertions.assertNotNull(capturedError);
        Assertions.assertTrue(capturedError.contains("Test Error"));
        Assertions.assertTrue(capturedError.contains("at setTimeout"));
    }

    @Test
    void testResourceLoadingError() {
        capturedError = null;

        // 5. Загрузка несуществующего скрипта
        page.navigate("data:text/html,"
                + "<script src='https://nonexistent-domain.com/404.js'></script>");

        page.waitForTimeout(1000);

        // 6. Проверка типа ошибки
        if (capturedError != null) {
            Assertions.assertTrue(capturedError.contains("Failed to load resource"));
        }
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }
}
