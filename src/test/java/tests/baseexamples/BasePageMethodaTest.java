package tests.baseexamples;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Page;

import tests.BaseTest;


public class BasePageMethodaTest extends BaseTest {

    @Test
    public void basePageMethodsTest() {

        // ===================== 1. Навигация =====================

        // page.navigate(url): Переход на указанный URL.
        page.navigate("https://ya.ru");

        // page.goBack(): Возврат на предыдущую страницу.
        page.goBack();

        // page.goForward(): Переход на следующую страницу.
        page.goForward();

        // page.reload(): Перезагрузка текущей страницы.
        page.reload();

        // ===================== 2. Работа с элементами =====================

        // page.click(selector): Клик по элементу.
        page.click("button#submit");

        // page.fill(selector, value): Заполнение поля ввода.
        page.fill("input#search", "Playwright");

        // page.type(selector, text): Посимвольный ввод текста.
        page.type("input#search", "Playwright");

        // page.check(selector): Выбор чекбокса или радиокнопки.
        page.check("input#agree");

        // page.uncheck(selector): Снятие выбора с чекбокса.
        page.uncheck("input#agree");

        // page.selectOption(selector, value): Выбор значения в выпадающем списке.
        page.selectOption("select#city", "Moscow");

        // page.hover(selector): Наведение курсора на элемент.
        page.hover("img#logo");

        // page.focus(selector): Установка фокуса на элемент.
        page.focus("input#email");


        // ===================== 3. Получение данных =====================

        // page.textContent(selector): Получение текстового содержимого элемента.
        String text = page.textContent("h1");

        // page.innerText(selector): Получение видимого текста элемента.
        String innerText = page.innerText("div#content");

        // page.getAttribute(selector, attribute_name): Получение значения атрибута элемента.
        String link = page.getAttribute("a#link", "href");

        // page.title(): Получение заголовка страницы.
        String title = page.title();

        // page.url(): Получение текущего URL.
        String url = page.url();


        // ===================== 4. Ожидания =====================

        // page.waitForSelector(selector): Ожидание появления элемента.
        page.waitForSelector("div#results");

        // page.waitForNavigation(): Ожидание завершения навигации.
        page.waitForNavigation(() -> page.click("a#next"));

        // page.waitForTimeout(milliseconds): Ожидание в течение указанного времени.
        page.waitForTimeout(2000);

        // ===================== 5. Скриншоты и видео =====================

        // page.screenshot(path): Создание скриншота страницы.
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshot.png")));

        // page.screenshot(selector, path): Создание скриншота элемента.
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshot.png")));

        // context.newPage().video().saveAs(path): Сохранение видео.
        page.video().saveAs(Path.of("....."));


        // ===================== 6. JavaScript в браузере =====================

        // page.evaluate(expression): Выполнение JavaScript.
        Object result = page.evaluate("document.title"); // Получение заголовка страницы

        // page.evaluateHandle(expression): Выполнение JavaScript и возврат handle.
        JSHandle handle = page.evaluateHandle("document.body"); // Получение handle для body

        // Handle (дескриптор) — это ссылка на объект в контексте браузера (например, DOM-элемент, функция, результат вычислений).
        // Он не содержит сам объект, а позволяет взаимодействовать с ним через API
        // (например, передавать между вызовами evaluate, вызывать методы, извлекать свойства), избегая копирования
        //  данных между браузером и скриптом. Это повышает эффективность работы с динамическими или тяжелыми объектами
    }

}
