package tests.browser;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Clock;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PropertiesExample {

    public static void main(String[] args) {

        // Properties - cвойства и события BrowserContext

        try (Playwright playwright = Playwright.create()) {
            BrowserType chrome = playwright.chromium();
            Browser browser = chrome.launch();
            BrowserContext context = browser.newContext();

            // ===================== Управление временем (Clock) =====================

            // Имитация времени (например, для тестирования таймеров).
            Clock clock = context.clock();

            // ===================== API-запросы (Request) =====================

            APIRequestContext request = context.request();
            // Отправка запроса с куками контекста
            APIResponse response = request.get("https://api.example.com/data");

            // ===================== Трассировка (Tracing) =====================

            Tracing tracing = context.tracing();
            tracing.start(new Tracing.StartOptions().setScreenshots(true));
            // ... действия ...
            tracing.stop(new Tracing.StopOptions().setPath(Paths.get("trace.zip")));

            // ===================== Фоновая страница (onBackgroundPage) =====================

            // Событие при создании фоновой страницы (только Chromium).
            context.onBackgroundPage(backgroundPage -> {
                System.out.println("Background page created: " + backgroundPage.url());
            });

            // ===================== Закрытие контекста (onClose) =====================

            // Событие при закрытии контекста.
            context.onClose(browserContext -> {
                System.out.println("Context closed!");
            });

            // ===================== Сообщения консоли (onConsoleMessage) =====================

            // Перехват console.log, console.error и др.
            context.onConsoleMessage(msg -> {
                System.out.println("Console: " + msg.text());
                // msg.args() - аргументы console.log()
            });

            // ===================== Диалоговые окна (onDialog) =====================

            // Без обработчика диалоги блокируют выполнение!
            context.onDialog(dialog -> {
                if (dialog.type().equals("alert")) {
                    dialog.accept(); // Подтвердить alert
                }
            });

            // ===================== Создание страницы (onPage) =====================

            // Событие при открытии новой вкладки.
            context.onPage(page -> {
                System.out.println("New page: " + page.url());
                page.waitForLoadState(); // Дождаться загрузки
            });

            // ===================== Сетевые события =====================

            // Запрос (onRequest):
            context.onRequest(req -> {
                System.out.println("Request: " + req.url());
            });

            // Ошибка запроса (onRequestFailed):
            context.onRequestFailed(req -> {
                System.out.println("Failed: " + req.url() + " - " + req.failure());
            });

            // Завершение запроса (onRequestFinished):
            context.onRequestFinished(req -> {
                System.out.println("Finished: " + req.url());
            });

            // Ответ (onResponse):
            context.onResponse(resp -> {
                if (resp.status() == 404) {
                    System.out.println("Not found: " + resp.url());
                }
            });

            // ===================== Необработанные ошибки (onWebError) =====================

            // Перехват необработанных исключений.
            context.onWebError(error -> {
                System.out.println("Uncaught error: " + error.error());
            });

            // ===================== Пример использования событий: =====================

            // Мониторинг 404 ошибок
            List<String> brokenLinks = new ArrayList<>();
            context.onResponse(resp -> {
                if (resp.status() == 404) {
                    brokenLinks.add(resp.url());
                }
            });

            // После выполнения теста
            assert brokenLinks.isEmpty() : "Found broken links: " + brokenLinks;

        }
    }
}
