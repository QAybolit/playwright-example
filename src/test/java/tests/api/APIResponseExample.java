package tests.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HttpHeader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class APIResponseExample {

    public static void main(String[] args) {

        /**
         * APIResponse - Представляет ответ на HTTP-запрос, выполненный через APIRequestContext. Содержит статус, заголовки и тело ответа.
         * Основные методы:
         * - byte[] data = response.body(); - Сырое тело ответа (байты)
         * - response.dispose(); - Освобождает ресурсы (обязательно для больших ответов!)
         * - String contentType = response.headers().get("content-type"); - Заголовки ответа (ключи в нижнем регистре)
         * - for (HttpHeader h : response.headersArray()) { ... } - Заголовки с сохранением оригинального регистра
         * - if (response.ok()) { ... } - true для статусов 200-299
         * - int status = response.status(); - 	HTTP-статус код
         * - String text = response.statusText(); - Текст статуса (например, "OK")
         * - String json = response.text(); - Текстовое представление тела
         * - String finalUrl = response.url(); - Финальный URL после редиректов
         */

        // ===================== 1. Базовый GET-запрос с проверкой =====================

        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext();

            // Отправка GET-запроса
            APIResponse response = request.get("https://jsonplaceholder.typicode.com/todos/1");

            // Проверка статуса
            if (response.ok()) {
                System.out.println("Status: " + response.status());
                System.out.println("Response: " + response.text());
            } else {
                System.err.println("Error: " + response.status() + " " + response.statusText());
            }

            // Освобождение ресурсов
            response.dispose();
            request.dispose();
        }

        // ===================== 2. Обработка заголовков =====================

        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext();
            APIResponse response = request.get("https://example.com");

            // Получение конкретного заголовка
            String contentType = response.headers().get("content-type");
            System.out.println("Content-Type: " + contentType);

            // Перебор всех заголовков
            for (Map.Entry<String, String> header : response.headers().entrySet()) {
                System.out.println(header.getKey() + ": " + header.getValue());
            }

            // Альтернативный вариант (с сохранением регистра)
            for (HttpHeader header : response.headersArray()) {
                System.out.println(header.name + " = " + header.value);
            }
        }

        // ===================== 3. Сохранение бинарных данных =====================

        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext();
            APIResponse response = request.get("https://example.com/image.png");

            if (response.ok() && response.headers().containsKey("content-type")) {
                byte[] imageData = response.body();
                Files.write(Paths.get("downloaded.png"), imageData);
            } else {
                System.err.println("Failed to download image");
            }

            // Обязательно освобождаем ресурсы!
            response.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ===================== 4. Обработка JSON =====================

        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = playwright.request().newContext();
            APIResponse response = request.get("https://jsonplaceholder.typicode.com/users/1");

            if (response.ok()) {
                // Используем Jackson для парсинга
                ObjectMapper mapper = new ObjectMapper();
                User user = mapper.readValue(response.text(), User.class);
                System.out.println("User name: " + user.name);
            } else {
                System.err.println("Failed to get user: " + response.status());
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}


// Модель данных
class User {
    public int id;
    public String name;
    public String email;
}
