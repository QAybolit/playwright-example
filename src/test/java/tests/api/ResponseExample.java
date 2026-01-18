package tests.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;

import java.util.Map;

public class ResponseExample {

    public static void main(String[] args) {

        /**
         * Объект Response представляет HTTP-ответы, полученные страницей или API-запросами.
         * Содержит методы для доступа к заголовкам, телу, статусу и другим метаданным ответа.
         * <br>
         * Ключевые методы:
         * - allHeaders()	Получает все HTTP-заголовки ответа (включая set-cookie). Имена заголовков в нижнем регистре.
         * - body()	Возвращает тело ответа в виде сырых байтов.
         * - finished()	Ожидает завершения ответа. Всегда возвращает null.
         * - frame()	Возвращает Frame, инициировавший этот ответ (для навигационных запросов).
         * - fromServiceWorker()	Проверяет, был ли ответ обработан Service Worker (через FetchEvent.respondWith).
         * - headerValue(name)	Возвращает значение первого заголовка по имени (без учёта регистра). Для set-cookie использует \n как разделитель.
         * - headerValues(name)	Возвращает все значения заголовка по имени (например, для set-cookie).
         * - headers()	Возвращает HTTP-заголовки (исключая security-заголовки). Имена в нижнем регистре.
         * - headersArray()	Возвращает все заголовки в исходном регистре. Для мульти-заголовков (например, set-cookie) — отдельные записи.
         * - ok()	Проверяет успешность ответа (код статуса в диапазоне 200-299).
         * - request()	Возвращает связанный объект Request.
         * - securityDetails()	Возвращает SSL-информацию о подключении.
         * - serverAddr()	Возвращает IP-адрес и порт сервера.
         * - status()	Возвращает HTTP-код статуса (например, 200).
         * - statusText()	Возвращает текст статуса (например, "OK").
         * - text()	Возвращает тело ответа как строку (декодирует байты в UTF-8).
         * - url()	Возвращает итоговый URL ответа (после перенаправлений).
         * <br>
         * Особенности работы
         * 1. Заголовки
         * - headers(): Не включает security-заголовки (например, Cookie).
         * - allHeaders(): Включает все заголовки, имена приводятся к нижнему регистру.
         * 2. Тело ответа:
         * - body() → сырые байты.
         * - text() → автоматическая декодировка в строку (UTF-8).
         * 3. Безопасность:
         * - securityDetails() доступен только для HTTPS-запросов.
         * 4. Service Workers:
         * - fromServiceWorker(): true, если ответ сгенерирован через FetchEvent.respondWith.
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            Response response = page.navigate("https://example.com");

            // Проверка успешности
            if (response.ok()) {
                System.out.println("Успешный ответ!");
            }

            // Получение статуса
            System.out.println("Статус: " + response.status()); // 200

            // Заголовки
            Map<String, String> headers = response.headers();
            System.out.println("Content-Type: " + headers.get("content-type"));

            // Тело ответа как строка
            String bodyText = response.text();
            System.out.println("Тело: " + bodyText.substring(0, 100) + "...");

            // Получение связанного запроса
            System.out.println("URL запроса: " + response.url());
        }

        /**
         * APIResponse и Response - это два разных класса для работы с HTTP-ответами, но в разных контекстах:
         * <br>
         * APIResponse - для API тестирования
         * APIResponse используется в контексте APIRequestContext для тестирования REST API:
         * Характеристики APIResponse:
         * - Возвращается из APIRequestContext методов
         * - Используется для тестирования API вне браузера
         * - Легковесный, нет накладных расходов браузера
         * - Поддерживает все HTTP методы (GET, POST, PUT, DELETE и т.д.)
         * - Идеально для изолированного тестирования API
         * <br>
         * Response - для браузерного тестирования
         * Response используется в контексте браузерной навигации и сетевых запросов:
         * Характеристики Response:
         * - Возвращается из браузерных операций (page.navigate())
         * - Перехватывается через обработчики событий
         * - Содержит информацию о запросах, сделанных браузером
         * - Используется для мониторинга сетевой активности
         * <br>
         * Используйте APIResponse, когда:
         * - Нужно протестировать API изолированно
         * - Требуется высокая скорость выполнения
         * - Не нужен UI/браузер
         * - Тестируете backend отдельно от frontend
         * <br>
         * Используйте Response, когда:
         * - Нужно проверить сетевые запросы в браузере
         * - Тестируете интеграцию frontend-backend
         * - Мониторите XHR/AJAX запросы
         * - Проверяете загрузку ресурсов (CSS, JS, изображения)
         */
    }
}
