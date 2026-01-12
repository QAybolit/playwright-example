package tests.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ClientCertificate;
import com.microsoft.playwright.options.HttpCredentials;
import com.microsoft.playwright.options.HttpCredentialsSend;
import com.microsoft.playwright.options.Proxy;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class APIRequestExample {

    public static void main(String[] args) {

        Playwright playwright = Playwright.create();

        // Класс APIRequest - Создание контекста для тестирования веб-API.
        // Получается через Playwright.request(), используется для инициализации APIRequestContext.

        // Создание контекста
        APIRequestContext context = playwright.request().newContext();

        // С опциями:
        APIRequestContext context1 = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.example.com")                                // Базовый URL для относительных путей
                        .setExtraHTTPHeaders(Map.of("Authorization", "Bearer token")) // Доп. заголовки для всех запросов
                        .setIgnoreHTTPSErrors(true)                                           // Игнорировать SSL-ошибки
                        .setStorageStatePath(Paths.get("auth.json"))                     // Путь к файлу с куки/LocalStorage
                        .setTimeout(20000)                                                    // Таймаут ответа в мс (по умолч. 30000)
                        .setUserAgent("MyBot/1.0")                                            // Кастомный User-Agent
        );


        // ===================== 1. Клиентские сертификаты =====================

        APIRequestContext context2 = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setClientCertificates(List.of(
                                new ClientCertificate("https://api.example.com") // сертификат принимает origin(целевой урл) как обязательный параметр
                                        .setCertPath(Paths.get("cert.pem"))       // Путь к PEM-файлу
                                        .setKeyPath(Paths.get("key.pem"))         // Путь к ключу
                                        .setPassphrase("secret")                       // Пароль (если есть)
                        ))
                        .setIgnoreHTTPSErrors(true)
        );

        // ===================== 2. HTTP Basic Auth  =====================

        APIRequestContext context3 = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setHttpCredentials(
                                new HttpCredentials("user", "password")
                                        .setOrigin("https://api.example.com") // Ограничение домена
                                        .setSend(HttpCredentialsSend.ALWAYS) // или UNAUTHORIZED
                        )
        );

        // ===================== 3. Прокси  =====================

        APIRequestContext context4 = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setProxy(
                                new Proxy("http://proxy.com:3128")  // HTTP/SOCKS5
                                        .setBypass(".com, .domain.com")    // Домены для обхода
                                        .setUsername("user")               // Аутентификация
                                        .setPassword("pass")
                        )
        );

        // ===================== Важные особенности  =====================

        // Относительные пути при setBaseURL("https://api.example.com/v1"):
        context.get("/users"); // → GET https://api.example.com/v1/users

        // Обработка ошибок При setFailOnStatusCode(true):
        context.get("/invalid-endpoint");  // Выбрасывает PlaywrightException при статусе 404/500

        // Сохранение сессии. Используйте setStorageStatePath() для повторного использования аутентификации:
        // После логина в браузере:
//        browserContext.storageState(new BrowserContext.StorageStateOptions()
//                .setPath(Paths.get("auth.json")));

        // В API-тестах:
//        .setStorageStatePath(Paths.get("auth.json"))
    }
}
