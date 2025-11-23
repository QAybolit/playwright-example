package tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DemoWaitsTest extends BaseTest {

    @Test
    public void waitsInRealScenarioTest() {
        // 1. АВТОМАТИЧЕСКИЕ ОЖИДАНИЯ
        page.navigate("https://demoqa.com/dynamic-properties");

        // кнопка станет активной через 5 сек - сработает автоожидание
        page.locator("#enableAfter").click();

        // Поле появится через 5 секунд
        page.locator("#visibleAfter").fill("Test");

        // 2. ЯВНЫЕ ОЖИДАНИЯ ДЛЯ СЛОЖНЫХ УСЛОВИЙ
        // Ждем появления элемента с таймаутом 7 сек.
        page.waitForSelector("#visibleAfter",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.valueOf("visible"))
                        .setTimeout(7000));

        // Ожидание появления css-свойства (кастомное условие)
        page.waitForFunction(
                "() => window.getComputedStyle(document.querySelector('#colorChange')).color === 'rgb(255, 0, 0)'",
                new Page.WaitForFunctionOptions().setTimeout(8000)
        );

        // Ожидание перехода на страницу
        page.waitForURL("**/checkout/confirmation",
                new Page.WaitForURLOptions().setTimeout(5000));

        // 3. ВСТРОЕННЫЕ АССЕРТЫ С ОЖИДАНИЯМИ
        // Проверка текста с автоматическим ожиданием
        assertThat(page.locator("#app > div > div > div.row > div.col-12.mt-4.col-md-6 > div"))
                .hasText("This text has random Id",
                        new LocatorAssertions.HasTextOptions().setTimeout(5000));

        // Проверка видимости и активности
        Locator checkoutButton = page.locator("#visibleAfter");

        // Отдельные ассерты для каждого условия
        assertThat(checkoutButton)
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));

        assertThat(checkoutButton)
                .isEnabled(new LocatorAssertions.IsEnabledOptions().setTimeout(3000));

        // Дополнительная проверка атрибута
        assertThat(checkoutButton)
                .hasAttribute("data-status", "active",
                        new LocatorAssertions.HasAttributeOptions().setTimeout(2000));
    }
}
