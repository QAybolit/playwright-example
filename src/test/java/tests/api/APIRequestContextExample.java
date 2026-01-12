package tests.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class APIRequestContextExample {

    public static void main(String[] args) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();

        // APIRequestContext - Отправка HTTP-запросов для тестирования API, интеграции с браузерным контекстом (куки, хранилище).

        // Связанный с браузером контекст (общие куки)
        BrowserContext browserContext = page.context();
        APIRequestContext apiContext = browserContext.request();

        // Изолированный контекст (собственные куки)
        APIRequestContext isolatedContext = playwright.request().newContext();

        /**
         * Ключевые особенности
         * 1. Интеграция с браузером:
         * - Запросы через browserContext.request() используют куки браузера
         * - Ответы с Set-Cookie обновляют браузерные куки
         * - Позволяет совмещать API-тесты и E2E-сценарии (например, аутентификация через API → действия в браузере)
         * 2. Изолированные контексты:
         * - Создаются через playwright.request().newContext()
         * - Имеют собственное хранилище куки
         * - Не влияют на состояние браузера
         */

        // ===================== 1. Общие параметры для всех методов =====================

        RequestOptions options = RequestOptions.create()
                .setHeader("Content-Type", "application/json") // Заголовки
                .setQueryParam("page", "2")                    // Query-параметры
                .setTimeout(15_000);                                       // Таймаут

        // ===================== 2. Отправка данных =====================

        // JSON - .setData(Map.of("title", "Book"))
        // Form URL-encoded - .setForm(FormData.create().set("key", "value"))
        // Multipart (файлы) - setMultipart()

        // Вариант 1: Из файла на диске
        Path filePath = Paths.get("data.csv");
        FormData formData = FormData.create().set("file", filePath);

        // Вариант 2: Из памяти
        FilePayload filePayload = new FilePayload(
                "script.js",
                "text/javascript",
                "console.log('Hello')".getBytes()
        );
        FormData formData1 = FormData.create().set("file", filePayload);

        // Отправка
        apiContext.post("/upload", RequestOptions.create().setMultipart(formData));

        // ===================== 3. HTTP-методы =====================

        /**
         * HTTP-методы
         * - delete(url) - Отправляет DELETE-запрос:  apiContext.delete("/users/5"))
         * - fetch(urlOrRequest) - Универсальный метод для любого HTTP-метода:  apiContext.fetch("https://api.com", options.setMethod("PUT"))
         * - get(url) - Отправляет GET-запрос:  apiContext.get("/posts", options.setQueryParam("id", "12"))
         * - head(url) - Отправляет HEAD-запрос:  apiContext.head("/resource")
         * - patch(url) - Отправляет PATCH-запрос:	apiContext.patch("/data", options.setData(updates))
         * - post(url) - Отправляет POST-запрос:  apiContext.post("/login", options.setForm(credentials))
         * - put(url) - Отправляет PUT-запрос:  apiContext.put("/items/3", options.setData(newData))
         */

        // ===================== 4. Управление ресурсами =====================

        // Освобождение ресурсов (обязательно!)
        apiContext.dispose();

        // С указанием причины (v1.45+)
        apiContext.dispose(new APIRequestContext.DisposeOptions().setReason("Тест завершен"));

        // ===================== 5. Сохранение состояния =====================

        // Сохранение куки/хранилища в файл
        apiContext.storageState(new APIRequestContext.StorageStateOptions()
                .setPath(Paths.get("api_state.json"))
//                .setIndexedDB(true) // Включая IndexedDB (v1.51+)
        );

        // Загрузка в другом контексте
        APIRequestContext newContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setStorageStatePath(Paths.get("api_state.json"))
        );

        // ===================== 6. Полный пример =====================

        try (Playwright playwright1 = Playwright.create()) {
            // Создаем изолированный контекст
            APIRequestContext context = playwright1.request().newContext();

            // POST с JSON
            Map<String, Object> bookData = Map.of("title", "Playwright Guide", "year", 2023);
            APIResponse createResponse = context.post("https://api.example.com/books",
                    RequestOptions.create().setData(bookData));

            // Проверка
            assert createResponse.status() == 201;

            // GET с параметрами
            APIResponse listResponse = context.get("https://api.example.com/books",
                    RequestOptions.create().setQueryParam("year", "2023"));

            // Сохранение состояния
            context.storageState(new APIRequestContext.StorageStateOptions()
                    .setPath(Paths.get("books_api_state.json")));

            // Освобождение ресурсов
            context.dispose();
        }
    }
}
