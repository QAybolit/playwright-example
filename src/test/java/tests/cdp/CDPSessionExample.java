package tests.cdp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.util.function.Consumer;

public class CDPSessionExample {

    public static void main(String[] args) {

        // CDPSession предоставляет прямой доступ к Chrome DevTools Protocol (CDP)
        // для низкоуровневых операций с браузерами на базе Chromium.

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Подключение к CDP =====================

            // Только для Chromium!
            CDPSession client = page.context().newCDPSession(page);

            // ===================== Отправка команд (send) =====================

            JsonObject response = client.send(
                    "Network.getResponseBody"
            );

            // ===================== Обработка событий (on) =====================

            client.on("Network.responseReceived", event -> {
                System.out.println("Получен ответ: " + event.get("response"));
            });

            client.on("Animation.animationCreated", event ->
                    System.out.println("Создана анимация: " + event)
            );

            // ===================== Отписка от событий (off) =====================

            Consumer<JsonObject> handler = event -> {
                // какой-то обработчик
            };
            client.on("Log.entryAdded", handler);

            // Отмена подписки
            client.off("Log.entryAdded", handler);

            // ===================== Отключение сессии (detach) =====================

            // После отключения сессия не может использоваться.
            client.detach();

            // ===================== Отправка команд CDP: =====================

            // Активация домена Runtime
            client.send("Runtime.enable");

            // Получение скорости воспроизведения анимации
            JsonObject response1 = client.send("Animation.getPlaybackRate");
            double speed = response1.get("playbackRate").getAsDouble();

            // Изменение скорости
            JsonObject params = new JsonObject();
            params.addProperty("playbackRate", speed / 2);
            client.send("Animation.setPlaybackRate", params);

            // ===================== Мониторинг производительности: =====================

            client.send("Performance.enable");
            client.on("Performance.metric", event -> {
                System.out.println("Метрика: " + event.get("name") + " = " + event.get("value"));
            });

            // ===================== Перехват сетевых запросов: =====================

            client.send("Network.enable");
            client.on("Network.requestWillBeSent", event -> {
                System.out.println("Запрос: " + event.get("request").getAsJsonObject().get("url"));
            });

            // ===================== Работа с DOM: =====================

            JsonObject document = client.send("DOM.getDocument");
            JsonElement nodeId = document.get("root").getAsJsonObject().get("nodeId");

            // Особенности и предупреждения
            // Только для Chromium: Не работает в Firefox/WebKit.
            // Жизненный цикл: Всегда закрывайте сессию через detach().

        }
    }
}
