package tests.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestOptionsExample {

    public static void main(String[] args) {

        /**
         * Класс RequestOptions настраивает параметры HTTP-запросов, отправляемых через APIRequestContext.
         * Playwright автоматически определяет content-type на основе переданных данных.
         * <br>
         * Ключевые методы:
         * 1. create()	Создает экземпляр RequestOptions.
         * 2. setData(data)	Устанавливает тело запроса.
         * • Для объектов: сериализует в JSON (content-type: application/json).
         * • Для строк/байтов: content-type: application/octet-stream.
         * 3. setForm(form)	Отправляет данные формы в кодировке application/x-www-form-urlencoded.
         * Автоматически устанавливает соответствующий content-type.
         * 4. setMultipart(form)	Отправляет данные формы (включая файлы) в кодировке multipart/form-data.
         * Автоматически устанавливает content-type.
         * 5. setQueryParam(name, value)	Добавляет параметр запроса в URL.
         * 6. setHeader(name, value)	Устанавливает HTTP-заголовок (применяется ко всем перенаправлениям).
         * 7. setMethod(method)	Задает метод запроса (например, POST, PUT).
         * 8. setFailOnStatusCode(boolean)	Генерировать ли исключение при кодах ответа вне диапазона 2xx-3xx. По умолчанию: false.
         * 9. setIgnoreHTTPSErrors(boolean)	Игнорировать ли ошибки HTTPS (например, самоподписанные сертификаты).
         * 10. setMaxRedirects(int)	Максимальное количество перенаправлений. По умолчанию: 20. 0 — отключить.
         * 11. setMaxRetries(int)	Максимальное количество повторов при сетевых ошибках (например, ECONNRESET). По умолчанию: 0 (без повторов).
         * 12. setTimeout(double)	Таймаут запроса в миллисекундах. По умолчанию: 30000 (30 сек). 0 — отключить.
         */

        try (Playwright playwright = Playwright.create()) {
            APIRequestContext context = playwright.request().newContext();

            // ===================== Отправка данных с параметром запроса: =====================
            context.post(
                    "https://example.com/submit",
                    RequestOptions.create()
                            .setQueryParam("page", 1)  // ?page=1
                            .setData("My data")                     // тело запроса
            );


            // ===================== Отправка формы (x-www-form-urlencoded): =====================
            context.post(
                    "https://example.com/signup",
                    RequestOptions.create().setForm(
                            FormData.create()
                                    .set("firstName", "John")
                                    .set("lastName", "Doe")
                    )
            );


            // ===================== Отправка файла (multipart/form-data): =====================
            // Из локального файла
            Path path = Paths.get("members.csv");
            APIResponse response = context.post(
                    "https://example.com/upload_members",
                    RequestOptions.create().setMultipart(
                            FormData.create().set("membersList", path)
                    )
            );

            // Создание файла вручную
            FilePayload filePayload = new FilePayload(
                    "members.csv",
                    "text/csv",
                    "Alice,33\nJohn,35\n".getBytes(StandardCharsets.UTF_8)
            );
            APIResponse response1 = context.post(
                    "https://example.com/upload_members",
                    RequestOptions.create().setMultipart(
                            FormData.create().set("membersList", filePayload)
                    )
            );
        }
    }
}
