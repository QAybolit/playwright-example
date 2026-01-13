package tests.baseexamples;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import tests.BaseTest;

public class WaitsTest extends BaseTest {

    @Test
    public void waitsTest() {

        /**
         * Playwright неявно применяет ожидания перед действиями и проверками:
         * button.click();  - Ждет видимости + кликабельности  
         * field.fill("text"); - Ждет доступности для ввода  
         * expect(header).toHaveText("Hello"); - Ждет появления текста
         *
         * Параметры по умолчанию:
         * - Таймаут: 30 секунд (настраивается глобально или локально).
         * - Проверяемые состояния: visible, enabled, stable, attached.
         *
         * Явные ожидания - Используются, когда нужно дождаться события, не связанного напрямую с элементом.
         * locator.waitFor()	Ждет конкретного состояния элемента
         * page.waitForSelector()	Ждет появления селектора
         */

        // ===================== 1. Явные ожидания =====================  

        // Ждем видимости элемента (макс 10 сек)
        Locator element = page.getByRole(AriaRole.BUTTON);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

        // Ждем исчезновения спиннера загрузки  
        page.waitForSelector(".spinner", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));

        // После клика ждем полной загрузки страницы 
        // page.waitForLoadState()	"domcontentloaded", "load", "networkidle"
        element.click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Ждем перехода на страницу профиля  
        // page.waitForURL()	Изменение URL (точное совпадение или regex)
        page.waitForURL("**/profile");

        // page.waitForFunction() выполняет JS-код до получения true
        // Ждем, пока в localStorage появится токен  
        page.waitForFunction("() => window.localStorage.getItem('authToken') !== null");

        // Ждем 5 элементов в списке  
        page.waitForFunction("() => document.querySelectorAll('.item').length === 5");


        // ===================== 2. Комбинирование ожиданий =====================  

        // Сценарий: Дождаться загрузки данных в таблице:
        // 1. Ждем исчезновения спиннера  
        page.waitForSelector(".spinner", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));

        // 2. Ждем появления строк  
        Locator tableRows = page.locator("table tr");
        tableRows.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // 3. Ждем минимум 5 строк  
        page.waitForFunction(
                "() => document.querySelectorAll('table tr').length >= 5",
                new Page.WaitForFunctionOptions().setTimeout(15000)
        );


        // ===================== 3. Ожидание сетевых запросов =====================  

        // Playwright позволяет дожидаться завершения сетевых запросов, что полезно для тестирования асинхронных операций.

        // page.waitForRequest(url): Ожидание запроса по указанному URL.

        // page.waitForResponse(url): Ожидание ответа по указанному URL.


        // Ожидание конкретного запроса
        page.waitForRequest(request ->
                        request.url().contains("/api/login"),
                () -> page.click("#login-button")
        );


        // С сохранением запроса для проверок
        Request loginRequest = page.waitForRequest(
                "**/api/login",
                () -> page.click("#login-button")
        );
        System.out.println("Method: " + loginRequest.method()); // POST
        System.out.println("Headers: " + loginRequest.headers());


        // С таймаутом
        page.waitForRequest(
                request -> request.url().endsWith(".json"),
                new Page.WaitForRequestOptions().setTimeout(10000),
                () -> page.click("#load-data")
        );


        // Ожидание ответа от API
        Response apiResponse = page.waitForResponse(
                "**/api/users/*",
                () -> page.click("#load-user")
        );
        System.out.println("Status: " + apiResponse.status()); // 200
        System.out.println("Body: " + apiResponse.text());


        // Фильтрация по статусу
        page.waitForResponse(response ->
                        response.url().contains("/api/data") &&
                                response.status() == 201,
                () -> page.click("#create-item")
        );

        /**
         * Ключевые моменты:
         *
         * - Паттерны URL: используйте ** для любого пути 
         * - Лямбда-фильтры: для сложных условий
         * - Таймауты: по умолчанию 30 секунд
         * - Комбинирование: часто используется вместе
         *
         */

        // ===================== 3. Лучшие практики =====================  

        // Избегайте Thread.sleep(): Используйте явные ожидания Playwright.

        // Настраивайте таймауты:

        // Локально (конкретное ожидание)  
        element.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        // Приоритет встроенных ожиданий: Не дублируйте waitFor перед click() – Playwright сделает это автоматически.
    }
}
