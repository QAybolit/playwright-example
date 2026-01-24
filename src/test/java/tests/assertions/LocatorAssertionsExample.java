package tests.assertions;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LocatorAssertionsExample {

    /**
     * LocatorAssertions предоставляет мощные методы для проверки состояния элементов на странице.
     * Все проверки автоматически ожидают выполнения условий с таймаутом по умолчанию 5 секунд.
     * <br>
     * Основные методы проверок:
     * - containsText(expected, options) Проверяет наличие текста (подстроки) в элементе.
     * - hasAccessibleDescription(description, options) Проверяет значение aria-description.
     * - hasAccessibleErrorMessage(error, options) Проверяет значение aria-errormessage.
     * - hasAccessibleName(name, options) Проверяет значение aria-label или aria-labelledby.
     * - hasAttribute(name, value, options) Проверяет значение атрибута.
     * - hasClass(expected, options) Проверяет точное соответствие классов (порядок важен).
     * - hasCount(count, options) Проверяет точное количество элементов, найденных локатором.
     * - hasCSS(name, value, options) Проверяет CSS-свойство элемента.
     * - hasId(id, options) Проверяет ID элемента.
     * - hasJSProperty(name, value, options) Проверяет JavaScript-свойство элемента.
     * - hasRole(role, options) Проверяет ARIA-роль элемента.
     * - hasText(expected, options) Проверяет точное соответствие текста элемента.
     * - hasValue(value, options) Проверяет значение input/textarea.
     * - hasValues(values, options) Проверяет выбранные значения в мультиселекте.
     * - isAttached() Проверяет, что элемент привязан к DOM или ShadowRoot.
     * - isChecked() Проверяет состояние чекбокса/радиокнопки.
     * - isDisabled() Проверяет, что элемент отключен (атрибут disabled или aria-disabled).
     * - isEditable() Проверяет, доступно ли редактирование элемента (например, поля ввода).
     * - isEmpty() Проверяет, что элемент не содержит текста (например, пустой div).
     * - isEnabled() Проверяет, что элемент активен (противоположность isDisabled).
     * - isFocused() Проверяет, что элемент в фокусе.
     * - isHidden() Проверяет, что элемент невидим (не привязан к DOM или скрыт).
     * - isInViewport() Проверяет, что элемент виден в области просмотра (viewport).
     * - isVisible() Проверяет, что элемент видим (привязан к DOM и не скрыт).
     * - matchesAriaSnapshot() Сравнивает доступность элемента (ARIA) с эталонным снимком.
     * - not() Инвертирует условие проверки.
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
        page.navigate("https://ui.example.com");
    }

    @Test
    void testElementProperties() {
        // 1. Проверка количества элементов
        Locator cards = page.locator(".dashboard-card");
        assertThat(cards).hasCount(4);

        // 2. Проверка CSS
        Locator primaryButton = page.locator(".btn-primary");
        assertThat(primaryButton)
                .hasCSS("font-weight", "700");

        // 3. Проверка ID
        Locator mainForm = page.locator("form");
        assertThat(mainForm).hasId("user-settings-form");

        // 4. Проверка JS-свойств
        Locator chart = page.locator("#data-chart");
        assertThat(chart).hasJSProperty("dataLoaded", true);

        // Проверки
        assertThat(page.locator("#hidden-element")).isHidden();
        assertThat(page.locator("#email-input")).isEditable();
        assertThat(page.locator("#submit-button")).isEnabled();
        assertThat(page.locator("#newsletter-checkbox")).isChecked();
        assertThat(page.locator("#empty-div")).isEmpty();
        assertThat(page.locator("#focused-field")).isFocused();
        assertThat(page.locator("#viewport-element")).isInViewport();
        assertThat(page.locator("#main-header")).isVisible();
        assertThat(page.locator("#attached-element")).isAttached();
        assertThat(page.locator("body")).matchesAriaSnapshot("...");

        // Отрицательная проверка
        assertThat(page.locator("#error-message")).not().isVisible();
    }

    @Test
    void testTextAndValues() {
        // 1. Проверка точного текста
        Locator header = page.locator(".page-header h1");
        assertThat(header).hasText("User Dashboard");

        // 2. Проверка значения поля
        Locator searchField = page.locator("#search");
        searchField.fill("playwright docs");
        assertThat(searchField).hasValue("playwright docs");

        // 3. Проверка мультиселекта
        Locator colorPicker = page.locator("#colors");
        colorPicker.selectOption(new String[]{"red", "green"});
        assertThat(colorPicker).hasValues(new String[]{"R", "G"});

        // 4. Проверка с регулярным выражением
        Locator timestamp = page.locator(".update-time");
        assertThat(timestamp).hasText(Pattern.compile("Updated: \\d{2}:\\d{2}"));
    }

    @Test
    void testARIA() {
        // 1. Проверка роли
        Locator modal = page.locator(".modal");
        assertThat(modal).hasRole(AriaRole.DIALOG);

        // 2. Проверка кастомной кнопки
        Locator customButton = page.locator(".custom-btn");
        assertThat(customButton)
                .hasAccessibleName("Save changes");

        assertThat(customButton)
                .hasRole(AriaRole.BUTTON);


        // 3. Проверка инвалидного поля
        Locator emailField = page.locator("#email");
        assertThat(emailField)
                .hasAttribute("aria-invalid", "true");

        assertThat(emailField)
                .hasAccessibleErrorMessage("Invalid email format");
    }

    @Test
    void testDynamicContent() {
        // 1. Инициирование действия
        page.locator("#refresh-btn").click();

        // 2. Проверка с кастомным таймаутом
        assertThat(page.locator(".status"))
                .hasText("Refreshed",
                        new LocatorAssertions.HasTextOptions().setTimeout(10_000));

        // 3. Проверка обновленного количества
        assertThat(page.locator(".data-row"))
                .hasCount(15,
                        new LocatorAssertions.HasCountOptions().setTimeout(5_000));
    }

    @Test
    void testFormElements() {
        // 1. Проверка текста
        Locator status = page.locator(".status");
        assertThat(status).containsText("Pending");

        // 2. Проверка классов
        Locator button = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Submit"));
        assertThat(button).hasClass("btn primary");

        // 3. Отправка формы
        button.click();

        // 4. Проверка изменений (с ожиданием)
        assertThat(status).hasText("Submitted",
                new LocatorAssertions.HasTextOptions().setTimeout(8000));

        // 5. Проверка атрибутов
        Locator emailInput = page.locator("#email");
        assertThat(emailInput)
                .hasAttribute("type", "email");

        assertThat(emailInput)
                .hasAttribute("required", "");

        // 6. Проверка доступности
        assertThat(page.locator(".error")).not().isVisible();
    }

    @Test
    void testAccessibilityAttributes() {
        Locator saveBtn = page.locator("#save-btn");
        Locator usernameInput = page.locator("#username");

        // 1. Проверка accessible name
        assertThat(saveBtn).hasAccessibleName("Save changes");

        // 2. Проверка описания
        assertThat(saveBtn).hasAccessibleDescription("Stores data to cloud");

        // 3. Имитация ошибки
        usernameInput.focus();
        page.keyboard().press("Tab");

        // 4. Проверка сообщения об ошибке
        assertThat(usernameInput).hasAccessibleErrorMessage(
                Pattern.compile("required", Pattern.CASE_INSENSITIVE));
    }

    @Test
    void testClassAssertions() {
        Locator item = page.locator(".list-item:first-child");

        // 1. Проверка точного совпадения классов
        assertThat(item).hasClass("list-item active");

        // 3. Проверка нескольких элементов
        Locator items = page.locator(".list-item");
        assertThat(items).hasClass(new String[] {
                "list-item",
                "list-item active",
                "list-item"
        });
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
