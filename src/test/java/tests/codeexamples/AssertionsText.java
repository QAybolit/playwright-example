package tests.codeexamples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import tests.BaseTest;

public class AssertionsText extends BaseTest {

    @Test
    public void assertionsTest() {

        // Вместо того чтобы сначала получать значения (например, isVisible(), textContent()), 
        // а затем проверять их с помощью JUnit/TestNG, в Playwright для Java используется прямой подход 
        // с PlaywrightAssertions.assertThat(), который автоматически обрабатывает ожидания и повторные попытки.

        // Ключевые преимущества:
        // - Автоматическое ожидание: Методы assertThat автоматически ждут, пока условие станет истинным 
        // (в течение таймаута), что решает проблему "flaky"-тестов.
        // - Выразительность: Код читается легко и понятно.
        // - Прямая работа с локаторами: Не нужно предварительно получать значения.

        // ===================== 1. Примеры с assertThat() =====================  

        page.navigate("http://example.com");

        // 1. Проверка видимости элемента
        PlaywrightAssertions.assertThat(page.locator("#submit-button")).isVisible();

        // 2. Проверка текста элемента
        PlaywrightAssertions.assertThat(page.locator("h1.title")).hasText("Добро пожаловать");

        // 3. Проверка атрибута
        PlaywrightAssertions.assertThat(page.locator("a#link")).hasAttribute("href", "/home");

        // 4. Проверка количества элементов
        PlaywrightAssertions.assertThat(page.locator(".item")).hasCount(3);

        // 5. Проверка отсутствия элемента
        PlaywrightAssertions.assertThat(page.locator("#deleted-element")).isHidden(); // Или .not().isVisible()

        // 6. Проверка значения инпута
        PlaywrightAssertions.assertThat(page.locator("input#email")).hasValue("user@example.com");

        // 7. Проверка заголовка страницы
        PlaywrightAssertions.assertThat(page).hasTitle("Главная страница");

        // 8. Проверка URL
        PlaywrightAssertions.assertThat(page).hasURL("https://example.com/home");


        // ===================== 2. Классический подход с JUnit/TestNG и AssertJ (Универсальный) =====================  

        page.navigate("http://example.com");

        // 1. Проверка видимости элемента (JUnit)
        assertTrue(page.locator("#submit-button").isVisible(),
                "Кнопка должна быть видимой");

        // 2. Проверка текста элемента (JUnit)
        String header = page.locator("h1.title").textContent();
        assertEquals("Добро пожаловать", header);

        // 3. Проверка количества элементов (JUnit)
        int count = page.locator(".item").count();
        assertEquals(3, count);


        // ===================== 3. Мягкие утверждения (Soft Assertions) =====================  

        // С AssertJ (для классического подхода):
        // Это надежный способ для мягких утверждений в Java.
        page.navigate("http://example.com");

        SoftAssertions softly = new SoftAssertions();

        // Получаем значения синхронно
        String productName = page.locator("#product-name").textContent();
        String price = page.locator("#price").textContent();

        // Проверяем их все
        softly.assertThat(productName).as("Название товара").isNotEmpty();
        softly.assertThat(price).as("Цена товара").matches("\\d+ руб.");

        // Не забываем вызвать assertAll() в конце!
        softly.assertAll();


        // ===================== 4. Список популярных утверждений в Playwright (Java) =====================  

        Locator element = page.getByAltText("Continue");

        // Элемент видим
        PlaywrightAssertions.assertThat(element).isVisible();

        // Элемент скрыт или отсутствует в DOM
        PlaywrightAssertions.assertThat(element).isHidden();

        // Элемент содержит точный текст
        PlaywrightAssertions.assertThat(element).hasText("Text");

        // Элемент содержит часть текста
        PlaywrightAssertions.assertThat(element).containsText("Text");

        // Поле ввода имеет значение
        PlaywrightAssertions.assertThat(element).hasValue("value");

        // Элемент имеет атрибут
        PlaywrightAssertions.assertThat(element).hasAttribute("attr", "value");

        // Список элементов имеет количество
        PlaywrightAssertions.assertThat(element).hasCount(5);

        // Чекбокс/радио выбран
        PlaywrightAssertions.assertThat(element).isChecked();

        // 	Элемент включен
        PlaywrightAssertions.assertThat(element).isEditable();

        // Элемент отключен
        PlaywrightAssertions.assertThat(element).isDisabled();

        // Страница имеет заголовок
        PlaywrightAssertions.assertThat(page).hasTitle("Title");

        // Страница имеет URL
        PlaywrightAssertions.assertThat(page).hasURL("URL");

        // HTTP-ответ успешен (статус 200...)
        // PlaywrightAssertions.assertThat(response).isOK();
    }
}
