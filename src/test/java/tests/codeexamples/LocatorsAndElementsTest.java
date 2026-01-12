package tests.codeexamples;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.SelectOption;

import tests.BaseTest;

public class LocatorsAndElementsTest extends BaseTest {

    @Test
    public void locatorsAndElementsTest() {

        /**
         * Локаторы — это объекты, позволяющие точно найти элементы DOM (например, кнопки, поля ввода, контейнеры)
         *
         * В Playwright локаторы решают главные задачи:
         * 1. Автоматические ожидания: локаторы автоматически ждут появления элементов и их видимости, 
         * исключая необходимость в статических задержках (Thread.sleep) или явных ожиданиях.
         *
         * 2. Устойчивость к изменениям: встроенные методы (getByRole(), getByText(), getByLabel() и др.) 
         * более надежны и легки для поддержки, чем простые CSS или XPath селекторы.
         *
         * 3. Читаемость и поддерживаемость: код с использованием читаемых локаторов проще понимать и сопровождать, 
         * например page.getByText("Submit") vs page.locator("//button[@class='btn']").
         *
         * Встроенные локаторы (рекомендуется)
         *
         * - getByText()	Ищет по точному или частичному тексту
         * - getByRole()	Ищет элементы по ARIA-роли
         * - getByLabel()	Ищет по тексту связанной метки <label>
         * - getByPlaceholder()	Поиск по атрибуту placeholder
         * - getByAltText()	Поиск по атрибуту alt
         * - getByTitle()	Поиск по атрибуту title
         * - getByTestId()	Поиск по атрибуту data-testid
         *
         * CSS-селектор
         * Locator submitButton = page.locator("button.submit");
         *
         * XPath-селектор
         * Locator header = page.locator("//h1[contains(text(),'Welcome')]");
         */

        // Для более точного позиционирования локаторы можно комбинировать и фильтровать
        Locator loginButton = page.getByRole(AriaRole.BUTTON)
                .filter(new Locator.FilterOptions().setHasText("Sign in"))
                .filter(new Locator.FilterOptions().setHas(page.locator("#login")));

        // Методы фильтрации:
        // .first() — первый элемент
        // .last() — последний элемент
        // .nth(int index) — элемент с индексом (индексация с нуля)
        // .filter(Locator.FilterOptions options) — фильтрация по условию


        /**
         * Действия — это методы, которые имитируют поведение пользователя:
         * Автоматические ожидания: Playwright выполняет действие только когда элемент готов (видим, включен, стабилен).
         * Встроенная отказоустойчивость: Автоматические повторы при временных сбоях (например, элемент перекрыт анимацией).
         * Поддержка современных фреймворков: Корректная работа с React/Vue.js (элементы обновляются без перезагрузки страницы).
         */


        // ===================== 1. Базовые действия =====================  

        // click() — основной метод для клика.
        Locator element = page.getByRole(AriaRole.TABLE);
        element.click(new Locator.ClickOptions()
                .setDelay(100)          // Задержка 100 мс между mousedown и mouseup
                .setButton(MouseButton.RIGHT)  // Правый клик (контекстное меню)
                .setForce(true)         // Кликнуть, даже если элемент перекрыт (осторожно!)
        );

        // dblclick() — двойной клик (например, для выделения слова).
        element.dblclick();

        // fill()	Быстрое заполнение поля
        element.fill("Hello");

        // type()	Имитация печати с клавиатуры
        element.type("World");

        // clear() Очистка поля
        element.clear();


        // ===================== 2. Работа с чекбоксами/радио-кнопками =====================  

        // Поставить галочку  
        Locator checkbox = page.getByRole(AriaRole.CHECKBOX);
        checkbox.check();

        // Снять галочку 
        checkbox.uncheck();

        // Проверить состояние  - true/false
        boolean isChecked = checkbox.isChecked();


        // ===================== 3. Выбор опций в <select> =====================  

        Locator select = page.getByRole(AriaRole.OPTION);

        // Выбор по значению (value)
        select.selectOption("ru");

        // Выбор по видимому тексту  
        select.selectOption(new SelectOption().setLabel("Русский"));

        // Выбор нескольких значений (для multiple)
        select.selectOption(new String[]{"en", "de"});


        // ===================== 4. Проверки состояний элементов =====================  

        // isVisible()	Видим ли элемент (не прозрачный, не скрыт CSS, имеет размеры).
        element.isVisible();

        // isHidden()	Скрыт ли элемент (display: none, visibility: hidden).
        element.isHidden();

        // isEnabled()	Доступен ли для действий (не имеет атрибута disabled).
        element.isEnabled();

        // isDisabled()	Заблокирован ли элемент.
        element.isDisabled();

        // isEditable()	Доступно ли поле для редактирования (input/textarea без readonly).
        element.isEditable();


        // ===================== 5. Продвинутые действия ===================== 

        // Наведение мыши - Имитирует hover (полезно для выпадающих меню)
        element.hover();

        // Фокус - Устанавливает фокус на элемент
        element.focus();

        // Загрузка файлов
        element.setInputFiles(Paths.get("/data/photo.png"));

        // Комбинации клавиш
        element.press("Control+A"); // Выделить всё  
        element.press("Tab");       // Перейти к следующему полю


        // ===================== 6. Лучшие практики ===================== 

        // Всегда проверяйте видимость перед действием
        // Избегайте force: true: Указывайте только для элементов вне viewport (например, в модальных окнах).
        // Используйте fill() вместо type() для заполнения форм — работает в 5 раз быстрее.
    }

}
