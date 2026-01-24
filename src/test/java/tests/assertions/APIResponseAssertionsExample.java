package tests.assertions;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class APIResponseAssertionsExample {

    /**
     * APIResponseAssertions предоставляет методы для проверки ответов API в тестах.
     * Класс используется вместе с PlaywrightAssertions.assertThat() для удобных assertions.
     * <br>
     * Основные методы:
     * - isOK()
     * Проверяет, что статус ответа находится в диапазоне 200-299 (успешные запросы).
     * - not()
     * Инвертирует следующую проверку (проверка на обратное условие).
     */

    // ===================== Полный пример тестового класса =====================
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testAPIResponses() {
        // 1. Успешный запрос
        APIResponse successResponse = page.request().get("https://api.example.com/data");
        assertThat(successResponse).isOK(); // Проверка 200-299

        // 2. Проверка контента
        String contentType = successResponse.headers().get("content-type");
        Assertions.assertEquals("application/json", contentType);

        // 3. Неуспешный запрос
        APIResponse errorResponse = page.request().get("https://api.example.com/not-found");

        // Проверка, что статус НЕ успешный
        assertThat(errorResponse).not().isOK();

        // Проверка конкретного статуса
        Assertions.assertEquals(404, errorResponse.status());
    }

    @Test
    void testAuthentication() {
        // Запрос с авторизацией
        APIResponse authResponse = page.request().get("https://api.example.com/protected",
                RequestOptions.create().setHeader("Authorization", "Bearer token"));

        // Проверка доступа
        if (authResponse.status() == 401) {
            assertThat(authResponse).not().isOK();
            System.out.println("Требуется аутентификация");
        } else {
            assertThat(authResponse).isOK();
        }
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
