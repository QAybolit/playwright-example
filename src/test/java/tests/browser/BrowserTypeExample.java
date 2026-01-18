package tests.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Geolocation;

import java.nio.file.Paths;
import java.util.List;

public class BrowserTypeExample {

    public static void main(String[] args) {

        /**
         * BrowserType — центральный класс для управления браузерами (Chromium, Firefox, WebKit).
         * Он позволяет:
         * - Запускать новые экземпляры браузеров
         * - Подключаться к уже работающим браузерам
         * - Работать с постоянными контекстами (сохранение сессий)
         */

        try (Playwright playwright = Playwright.create()) {

            // ===================== Подключение к браузеру (connect) =====================

            Browser browser = playwright.chromium().connect(
                    "ws://localhost:9222/devtools/browser/...",
                    new BrowserType.ConnectOptions()
                            .setSlowMo(500) // Замедление для отладки
            );

            // ===================== Подключение через Chrome DevTools (connectOverCDP) =====================

            // Только для Chromium!
            Browser browser1 = playwright.chromium().connectOverCDP(
                    "http://localhost:9222",
                    new BrowserType.ConnectOverCDPOptions()
                            .setTimeout(60_000) // Таймаут 60 сек
            );

            // ===================== Путь к исполняемому файлу (executablePath) =====================

            // Возвращает путь к браузеру из комплекта Playwright.
            String path = playwright.chromium().executablePath();
            System.out.println("Путь к Chromium: " + path);

            // ===================== Запуск браузера (launch) =====================

            // Особенности для Chromium:
            // Используйте setIgnoreDefaultArgs(List.of("--mute-audio")) для фильтрации аргументов
            // Для Chrome: setChannel("chrome-canary")
            Browser browser2 = playwright.firefox().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false) // Режим с GUI
                            .setArgs(List.of("--start-maximized"))
                            .setChannel("firefox-dev") // Канал Firefox Dev
                            .setDownloadsPath(Paths.get("downloads/"))
            );

            // ===================== Постоянный контекст (launchPersistentContext) =====================

            // Особенности:
            // Автоматически сохраняет данные между запусками
            // При закрытии контекста браузер завершает работу
            BrowserContext context = playwright.chromium().launchPersistentContext(
                    Paths.get("user-data-dir/"), // Директория профиля
                    new BrowserType.LaunchPersistentContextOptions()
                            .setViewportSize(1920, 1080)
                            .setGeolocation(new Geolocation(59.95, 30.31667))
                            .setPermissions(List.of("geolocation"))
            );

            // ===================== Имя браузера (name) =====================

            String browserName = playwright.webkit().name(); // "webkit"

            // ===================== Важные предупреждения!!! =====================

            // Пользовательские аргументы: Используйте с осторожностью!
            // Рискованно! Может сломать Playwright
//            .setArgs(List.of("--unsafe-flag"))


            // Прокси-аутентификация:
//            .setProxy(new Proxy("http://proxy.com:3128")
//                    .setUsername("user")
//                    .setPassword("pass"))

            // Сервис-воркеры:
//            .setServiceWorkers(ServiceWorkerPolicy.BLOCK) // Блокировка

            // Режимы HAR:
//            .setRecordHarPath(Paths.get("network.har"))
//                    .setRecordHarMode(HarMode.MINIMAL) // Только для маршрутизации
        }
    }
}
