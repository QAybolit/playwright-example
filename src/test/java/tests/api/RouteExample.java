package tests.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;

import java.util.HashMap;
import java.util.Map;

public class RouteExample {

    public static void main(String[] args) {

        /**
         * Объект Route позволяет перехватывать и обрабатывать сетевые запросы через page.route() или browserContext.route().
         * <br>
         * Ключевые методы
         * - abort([errorCode])	Прерывает запрос
         * - fallback([options])	Передаёт запрос следующему обработчику
         * - fetch([options])	Выполняет запрос и возвращает ответ
         * - fulfill([options])	Завершает запрос с кастомным ответом
         * - request()	Возвращает связанный объект Request
         * - resume([options])	Отправляет запрос в сеть (минуя другие обработчики)
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // ===================== Порядок обработчиков: =====================
            // Обработчики выполняются в обратном порядке регистрации (последний зарегистрированный → первый вызванный).
            // fallback() передаёт управление следующему обработчику, resume() и fulfill() завершают цепочку.


            // ===================== Модификация запросов: =====================
            // Добавление заголовка через fallback
            page.route("**/*", route -> {
                Map<String, String> headers = new HashMap<>(route.request().headers());
                headers.put("X-Custom", "value");
                route.fallback(new Route.FallbackOptions().setHeaders(headers));
            });


            // ===================== Модификация ответов: =====================
            // Перехват + изменение JSON
            page.route("https://api.example.com/data", route -> {
                APIResponse response = route.fetch();
                JsonObject json = new Gson().fromJson(response.text(), JsonObject.class);
                json.addProperty("modified", true);
                route.fulfill(new Route.FulfillOptions()
                        .setResponse(response)
                        .setBody(json.toString()));
            });


            // ===================== Фильтрация запросов: =====================
            // Обработка только POST-запросов
            page.route("**/*", route -> {
                if (!"POST".equals(route.request().method())) {
                    route.fallback();
                    return;
                }
                // Логика для POST
            });


            // ===================== Порядок регистрации маршрутов: =====================
            // Обработчик 1 (выполнится последним)
            // Для fetch() используйте try-catch, чтобы обработать сетевые ошибки.
            // При abort() браузер генерирует событие Failed для запроса.
            page.route("**/*", route -> route.abort());

            // Обработчик 2 (выполнится вторым)
            page.route("**/*", route -> route.fallback());

            // Обработчик 3 (выполнится первым)
            page.route("**/*", route -> route.fallback());


            // ===================== пример =====================
            // 1. Прерывание запросов к /blockme
            // Все запросы к /blockme прерываются с ошибкой.
            // В консоли браузера: Blocked: TypeError: Failed to fetch.
            page.route("**/blockme", route -> {
                System.out.println("Blocked: " + route.request().url());
                route.abort();
            });

            // 2. Модификация заголовков для /api/users
            // Запросы к /api/users получают заголовок X-Auth: token.
            // Запрос передаётся дальше (на сервер или другие обработчики).
            page.route("**/api/users", route -> {
                Map<String, String> headers = new HashMap<>(route.request().headers());
                headers.put("X-Auth", "token");
                route.fallback();
            });

            // 3. Мокирование ответа для /api/data
            // Запросы к /api/data получают кастомный JSON: {"data":"mocked"}.
            // Статус ответа: 200, Content-Type: application/json.
            page.route("**/api/data", route -> {
                JsonObject mockJson = new JsonObject();
                mockJson.addProperty("data", "mocked");
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setContentType("application/json")
                        .setBody(mockJson.toString()));
            });

            // 4. Использование fetch для модификации ответа
            // Запрос к Dog API выполняется, ответ модифицируется (добавляется поле new_breed).
            // В консоли браузера: Dog API: [].
            page.route("https://dog.ceo/api/breeds/list/all", route -> {
                APIResponse response = route.fetch();
                JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
                json.getAsJsonObject("message").add("new_breed", new JsonArray());
                route.fulfill(new Route.FulfillOptions()
                        .setResponse(response)
                        .setBody(json.toString()));
            });

            // Имитация запросов из браузера
            page.evaluate("() => {\n" +
                    "  fetch('/blockme').catch(e => console.log('Blocked: ' + e));\n" +
                    "  fetch('/api/users').then(r => console.log('Users: ' + r.status));\n" +
                    "  fetch('/api/data').then(r => r.json()).then(d => console.log('Data: ' + JSON.stringify(d)));\n" +
                    "  fetch('https://dog.ceo/api/breeds/list/all').then(r => r.json()).then(d => console.log('Dog API: ' + d.message.new_breed));\n" +
                    "}");

            // Ожидание выполнения запросов
            page.waitForTimeout(3000);
        }
    }
}
