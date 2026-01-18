package tests.api;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.HttpHeader;
import com.microsoft.playwright.options.Sizes;
import com.microsoft.playwright.options.Timing;

import java.util.List;
import java.util.Map;

public class RequestExample {

    public static void main(String[] args) {

        /**
         * Request - Представляет сетевой запрос, инициированный страницей.
         * При отправке запроса генерируется последовательность событий:
         * - Page.onRequest() - при инициации запроса
         * - Page.onResponse() - при получении статуса/заголовков
         * - Page.onRequestFinished() - при полной загрузке ответа
         * Если запрос завершается ошибкой, вместо requestfinished генерируется Page.onRequestFailed().
         * <br>
         * Особенности:
         * - HTTP-ошибки (404, 503) считаются успешными запросами
         * - Перенаправления создают новые объекты Request
         * - Предоставляет детальную информацию о размерах и времени выполнения
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Обработка событий запросов
            page.onRequest(request -> {

                // ===================== allHeaders()  =====================
                // Возвращает все заголовки запроса (включая security-заголовки). Имена в нижнем регистре.
                Map<String, String> headers = request.allHeaders();


                // ===================== frame() =====================
                // Возвращает фрейм, инициировавший запрос.
                Frame frame = request.frame();
                System.out.println("Request from frame: " + frame.name());


                // ===================== headerValue(name) =====================
                // Возвращает значение конкретного заголовка (регистронезависимо).
                String contentType = request.headerValue("Content-Type");


                // ===================== headers() =====================
                // Возвращает заголовки запроса (без security-заголовков). Имена в нижнем регистре.
                Map<String, String> headers1 = request.headers();


                // ===================== headersArray() =====================
                // Возвращает массив заголовков с сохранением оригинального регистра.
                List<HttpHeader> headers2 = request.headersArray();
                headers2.forEach(header ->
                        System.out.println(header.name + ": " + header.value)
                );


                // ===================== isNavigationRequest() =====================
                // Проверяет, является ли запрос навигационным.
                if (request.isNavigationRequest()) {
                    System.out.println("Main navigation: " + request.url());
                }


                // ===================== method() =====================
                // Возвращает HTTP-метод запроса.
                String method = request.method(); // "GET", "POST", etc.


                // ===================== postData() =====================
                // Возвращает тело запроса в виде строки.
                String body = request.postData();


                // ===================== postDataBuffer() =====================
                // Возвращает тело запроса в бинарном виде.
                byte[] data = request.postDataBuffer();


                // ===================== redirectedFrom() =====================
                // Возвращает исходный запрос для перенаправления.
                Request original = request.redirectedFrom();
                if (original != null) {
                    System.out.println("Redirected from: " + original.url());
                }


                // ===================== redirectedTo() =====================
                // Возвращает запрос после перенаправления.
                Request next = request.redirectedTo();
                if (next != null) {
                    System.out.println("Redirected to: " + next.url());
                }


                // ===================== resourceType() =====================
                // Возвращает тип ресурса
                String type = request.resourceType(); // "script", "image", etc.


                // ===================== response() =====================
                // Возвращает связанный ответ.
                Response response = request.response();
                if (response != null) {
                    System.out.println("Status: " + response.status());
                }


                // ===================== sizes() =====================
                // Возвращает информацию о размерах запроса/ответа.
                Sizes sizes = request.sizes();
                System.out.println("Request size: " + sizes.requestBodySize + " bytes");
                System.out.println("Response size: " + sizes.responseBodySize + " bytes");


                // ===================== timing() =====================
                // Возвращает временные метки выполнения запроса.
                Timing timing = request.timing();
                double totalTime = timing.responseEnd - timing.startTime;
                System.out.println("Request took: " + totalTime + "ms");
                // Timing объект с полями:
                // startTime: double
                // domainLookupStart: double
                // domainLookupEnd: double
                // connectStart: double
                // secureConnectionStart: double
                // connectEnd: double
                // requestStart: double
                // responseStart: double
                // responseEnd: double


                // ===================== url() =====================
                // Возвращает URL запроса.
                String url = request.url();
            });

            // ===================== failure() =====================
            // Возвращает описание ошибки для неудачных запросов.
            page.onRequestFailed(request -> {
                System.out.println("Failed: " + request.url() + " - " + request.failure());
            });
        }

        // ===================== Пример использования =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Обработка событий запросов
            page.onRequest(request -> {
                System.out.println("Request URL: " + request.url());
                System.out.println("Request Method: " + request.method());
                System.out.println("Is Navigation Request: " + request.isNavigationRequest());
            });

            page.onResponse(response -> {
                System.out.println("Response URL: " + response.url());
                System.out.println("Response Status: " + response.status());
            });

            page.onRequestFailed(request -> {
                System.out.println("Request Failed: " + request.url() + " - " + request.failure());
            });

            // Навигация к странице
            page.navigate("https://demoqa.com/automation-practice-form");

            // Получение информации о запросах
            page.onRequestFinished(request -> {
                System.out.println("Request Finished: " + request.url());
                System.out.println("Response: " + request.response());
            });

            // Завершение работы
            browser.close();
        }

    }
}
