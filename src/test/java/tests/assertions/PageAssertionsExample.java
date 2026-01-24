package tests.assertions;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PageAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PageAssertionsExample {

    /**
     * PageAssertions предоставляет методы для проверки состояния страницы в тестах.
     * Класс используется с PlaywrightAssertions.assertThat() для удобных проверок навигации и содержимого страницы.
     * <br>
     * Основные методы:
     * - hasTitle(titleOrRegExp, options)
     * Проверяет соответствие заголовка страницы строке или регулярному выражению.
     * - hasURL(urlOrRegExp, options)
     * Проверяет соответствие URL страницы строке или регулярному выражению.
     * - not()
     * Инвертирует следующую проверку.
     * <br>
     * Опции проверок:
     * - setTimeout(double): Максимальное время ожидания (мс)
     * - setIgnoreCase(boolean): Сравнение без учета регистра (только для hasURL)
     * - По умолчанию: timeout = 5000 мс, ignoreCase = false
     * <br>
     * Гибкие проверки:
     * - Точное совпадение: hasTitle("Login Page")
     * - Регулярные выражения: hasTitle(Pattern.compile(".*Dashboard"))
     * - Игнорирование регистра: setIgnoreCase(true)
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
    void testPageNavigation() {
        // 1. Переход на страницу
        page.navigate("https://example.com/login");

        // 2. Проверка URL
        assertThat(page).hasURL("https://example.com/login");
        assertThat(page).hasURL(Pattern.compile(".*/login"),
                new PageAssertions.HasURLOptions().setIgnoreCase(true));

        // 3. Проверка заголовка
        assertThat(page).hasTitle("Login Portal");

        // 4. Заполнение формы
        page.fill("#username", "testuser");
        page.fill("#password", "password123");
        page.click("#submit-btn");

        // 5. Проверка перенаправления (с таймаутом 8 сек)
        assertThat(page).hasURL(Pattern.compile(".*/dashboard"),
                new PageAssertions.HasURLOptions().setTimeout(8000));

        // 6. Проверка заголовка после навигации
        assertThat(page).hasTitle(Pattern.compile("User Dashboard"));

        // 7. Отрицательная проверка
        assertThat(page).not().hasURL(".*/login"); // Уже не на странице логина
    }

    @Test
    void testAccessDenied() {
        page.navigate("https://example.com/admin");

        // Проверка страницы ошибки
        assertThat(page).hasTitle(Pattern.compile("Access Denied|Error"));
        assertThat(page).hasURL(Pattern.compile(".*/error"));

        // Проверка без учета регистра
        assertThat(page).hasURL("HTTPS://EXAMPLE.COM/ERROR",
                new PageAssertions.HasURLOptions().setIgnoreCase(true));
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
