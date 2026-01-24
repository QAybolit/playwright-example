package tests.assertions;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightAssertionsExample {

    /**
     * PlaywrightAssertions — центральный класс для веб-ориентированных проверок в Playwright.
     * Он предоставляет:
     * - Автоматическое ожидание условий (retry-логика)
     * - Удобные методы для создания assertions
     * - Глобальную настройку времени ожидания
     * <br>
     * Основные методы:
     * - assertThat(APIResponse response) Создает проверки для HTTP-ответов.
     * - assertThat(Locator locator) Создает проверки для элементов страницы.
     * - assertThat(Page page) Создает проверки для страницы.
     * - setDefaultAssertionTimeout(timeout) Устанавливает глобальное время ожидания для всех проверок (по умолчанию 5000 мс).
     * <br>
     * Проверки повторяются до выполнения условия или истечения таймаута
     * Не требует ручного добавления wait-команд
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

        // Установка глобального таймаута
        PlaywrightAssertions.setDefaultAssertionTimeout(10_000); // 10 секунд
    }

    @Test
    void testUserWorkflow() {
        // 1. Проверка страницы
        page.navigate("https://app.example.com");
        assertThat(page).hasURL("https://app.example.com/login");

        // 2. Проверка элементов
        Locator usernameField = page.locator("#username");
        assertThat(usernameField).isVisible();

        // 3. Взаимодействие с элементами
        usernameField.fill("testuser");
        page.locator("#password").fill("password123");
        page.locator("#login-btn").click();

        // 4. Проверка изменения состояния (с автоматическим ожиданием)
        assertThat(page).hasURL("https://app.example.com/dashboard");
        assertThat(page.locator(".welcome-msg")).hasText("Welcome, testuser!");

        // 5. Проверка API-ответа
        APIResponse profileResponse = page.request().get("https://api.example.com/profile");
        assertThat(profileResponse).isOK();
    }

    @Test
    void testDynamicContent() {
        page.navigate("https://app.example.com/feed");

        // 1. Проверка начального состояния
        Locator postList = page.locator(".posts");
        assertThat(postList).hasCount(5);

        // 2. Инициирование изменения
        page.locator("#load-more").click();

        // 3. Проверка обновленного состояния (ждет до 10 сек)
        assertThat(postList).hasCount(10);

        // 4. Проверка с кастомным таймаутом
        assertThat(page.locator(".updated-time"))
                .hasText("Updated just now",
                        new LocatorAssertions.HasTextOptions().setTimeout(3_000));
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();

        // Сброс таймаута (опционально)
        PlaywrightAssertions.setDefaultAssertionTimeout(5_000);
    }
}
