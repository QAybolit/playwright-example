package tests.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.WebSocketRoute;

public class WebSocketRouteExample {

    /**
     * WebSocketRoute позволяет перехватывать и управлять WebSocket-соединениями:
     * - Полное мокирование трафика (без подключения к серверу)
     * - Перехват и модификацию сообщений при подключении к реальному серверу
     * <br>
     * Основные методы:
     * - connectToServer()
     * Подключается к реальному серверу вместо мокирования.
     * Возвращает: WebSocketRoute (серверная сторона)
     * - onMessage(handler)
     * Обрабатывает входящие сообщения (отменяет автоматическую пересылку).
     * - send(message)
     * Отправляет сообщение (текст или бинарные данные).
     * - close(options)
     * Закрывает соединение с указанием кода и причины.
     * - onClose(handler)
     * Обрабатывает событие закрытия соединения.
     * - url()
     * Возвращает URL WebSocket-соединения.
     * <br>
     * Особенности работы:
     * - Сообщения пересылаются автоматически, пока не вызван onMessage()
     * - Сообщения пересылаются автоматически, пока не вызван onMessage()
     * - close() позволяет симулировать нестандартное закрытие
     * - onClose() перехватывает события закрытия
     */

    public static void main(String[] args) {

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Полное мокирование WebSocket
            page.routeWebSocket("wss://demo.com/ws", route -> {
                route.onMessage(frame -> {
                    if ("ping".equals(frame.text())) {
                        route.send("pong"); // Отправляем мок-ответ
                    }
                });
            });

            // Перехват с модификацией сообщений
            page.routeWebSocket("/api/ws", route -> {
                WebSocketRoute server = route.connectToServer();

                // Перехват сообщений от клиента
                route.onMessage(frame -> {
                    String modified = frame.text().replace("test", "prod");
                    server.send(modified); // Отправляем модифицированное сообщение
                });

                // Перехват ответов сервера
                server.onMessage(frame -> {
                    if (frame.text().contains("error")) {
                        route.send("custom error"); // Заменяем ошибку
                    } else {
                        route.send(frame.text()); // Пересылаем без изменений
                    }
                });
            });

            // Блокировка сообщений
            page.routeWebSocket("**/chat", route -> {
                WebSocketRoute server = route.connectToServer();

                route.onMessage(frame -> {
                    if (!frame.text().contains("blocked")) {
                        server.send(frame.text()); // Фильтрация исходящих
                    }
                });

                server.onMessage(frame -> {
                    if (!frame.text().contains("secret")) {
                        route.send(frame.text()); // Фильтрация входящих
                    }
                });
            });
        }

        // ===================== Полный пример тестового класса =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Мокирование WebSocket без подключения к серверу
            page.routeWebSocket("wss://mock-server.com/ws", route -> {
                route.onMessage(frame -> {
                    if ("request".equals(frame.text())) {
                        route.send("mocked-response");
                    }
                });
            });

            page.navigate("https://app-using-websocket.com");
            page.click("#connect-btn");

            // Здесь будут проверки реакции на "mocked-response"

            // Перехват с подключением к реальному серверу
            page.routeWebSocket("**/data-stream", route -> {
                WebSocketRoute server = route.connectToServer();

                // Модификация исходящих сообщений
                route.onMessage(frame -> {
                    JsonObject json = JsonParser.parseString(frame.text()).getAsJsonObject();
                    json.addProperty("timestamp", System.currentTimeMillis());
                    server.send(json.getAsString());
                });

                // Фильтрация входящих сообщений
                server.onMessage(frame -> {
                    if (!frame.text().contains("sensitive")) {
                        route.send(frame.text());
                    }
                });
            });

            page.navigate("https://data-dashboard.com");
            // Действия, инициирующие обмен данными
        }
    }
}
