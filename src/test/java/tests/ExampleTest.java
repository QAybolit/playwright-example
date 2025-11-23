package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest extends BaseTest {

    @Test
    public void exampleTest() {
        // 1. Навигация и явное ожидание
        page.navigate("https://demoqa.com");
        page.waitForSelector(".card", new Page.WaitForSelectorOptions().setTimeout(10000));

        // 2. ЛОКАТОРЫ
        // css селектор
        Locator elementsCard = page.locator("div.card:has-text('Elements')");
        elementsCard.click();

        // Поиск по тексту
        page.locator("li.btn-light:has-text('Text Box')").click();

        // Поиск по роли ARIA (лучшая практика)
        Locator fullNameLabel = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Full Name"));

        // 3. ВЗАИМОДЕЙСТВИЕ С ЭЛЕМЕНТАМИ
        // Fill vs Type
        fullNameLabel.fill("Иван Петров"); // Быстрое заполнение

        Locator emailInput = page.locator("#userEmail");
        emailInput.type("text@example.com"); // Посимвольный ввод, deprecated

        Locator addressArea = page.locator("#currentAddress");
        addressArea.fill("ул. Ленина, д. 21");

        // клик по кнопке
        Locator submitButton = page.locator("#submit");
        submitButton.click();

        // 4. ПРОВЕРКИ И ПОЛУЧЕНИЕ ДАННЫХ
        // Ожиданиие появления результата
        page.waitForSelector("#output");

        // Проверка текста
        Locator nameResult = page.locator("#name");
        assertTrue(nameResult.textContent().contains("Иван Петров"), // textContent() или innerText()
                "Неверное имя в результате");

        // Проверка атрибута
        Locator emailResult = page.locator("#email");
        assertEquals("text@example.com",
                emailResult.textContent().replace("Email:", "").trim(),
                "Неверный email в результате");

        // 5. РАБОТА С ЧЕКБОКСАМИ И РАДИОКНОПКАМИ
        page.locator("li:has-text('Check Box')").click();

        // Чекбоксы
        Locator homeCheckbox = page.locator("label:has-text('Home') .rct-checkbox");
        homeCheckbox.check();
        assertTrue(homeCheckbox.isChecked(), "Чекбокс Home должен быть выбран");

        // Радио-кнопки
        page.locator("li:has-text('Radio Button')").click();
        Locator impressiveRadio = page.locator("label:has-text('Impressive')");
        impressiveRadio.check();
        assertTrue(impressiveRadio.isChecked(),
                "Радио-кнопка Impressive должна быть выбрана");
    }
}
