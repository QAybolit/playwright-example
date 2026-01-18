package tests.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Proxy;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class BrowserExample {

    public static void main(String[] args) {

        /**
         * Browser - Представляет экземпляр браузера (Chromium, Firefox, WebKit).
         * Создается через BrowserType.launch(). Используется для создания изолированных контекстов.
         * <br>
         * Методы:
         * - chromium(), firefox(), webkit() - Возвращает тип браузера
         * - close() - Принудительно закрывает браузер со всеми страницами
         * - contexts() - Список активных контекстов
         * - isConnected() - Проверяет активность соединения (true = браузер запущен)
         * - newBrowserCDPSession()	 - Создает сессию Chrome DevTools (только Chromium!)
         * - newContext() - Создает изолированный контекст (куки/кеш не разделяются)
         */

        // ===================== Создание браузера: базовый пример =====================

        try (Playwright playwright = Playwright.create()) {
            // Выбор типа браузера
            BrowserType firefox = playwright.firefox();

            // Запуск с опциями
            Browser browser = firefox.launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setTimeout(30_000));

            // Создание контекста
            BrowserContext context = browser.newContext();

            // Создание страницы
            Page page = context.newPage();
            page.navigate("https://example.com");

            // Корректное закрытие
            context.close();
            browser.close();
        }

        // ===================== Ключевые опции для newContext() =====================

        try (Playwright playwright = Playwright.create()) {
            BrowserType chrome = playwright.chromium();
            Browser browser = chrome.launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setTimeout(30_000));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    // Базовые настройки
                    .setBaseURL("https://api.example.com")
                    .setUserAgent("Custom UA")
                    .setViewportSize(1280, 720)

                    // Безопасность
                    .setIgnoreHTTPSErrors(true)
                    .setJavaScriptEnabled(false)

                    // Эмуляция
                    .setIsMobile(true)
                    .setDeviceScaleFactor(2.0)
                    .setLocale("ru-RU")

                    // Сеть
                    .setExtraHTTPHeaders(Map.of("Authorization", "Bearer token"))
                    .setProxy(new Proxy("http://proxy:8080"))

                    // Авторизация
                    .setHttpCredentials("user", "pass")

                    // Запись действий
                    .setRecordHarPath(Paths.get("session.har"))
                    .setRecordVideoDir(Paths.get("videos/"))

            );

            // ===================== Управление контекстами =====================

            // Создание изолированного контекста
            BrowserContext privateContext = browser.newContext();

            // Получение списка активных контекстов
            List<BrowserContext> activeContexts = browser.contexts();

            // Закрытие контекста (рекомендуется явно)
            privateContext.close();

            // ===================== CDP Session (только Chromium) =====================

            if (browser.browserType().name().equals("chromium")) {
                CDPSession cdp = browser.newBrowserCDPSession();
                cdp.send("Network.enable");
                cdp.on("Network.requestWillBeSent", event ->
                        System.out.println("Request: " + event.get("request") + " " + event.get("url")));
            }

            // ===================== Безопасное закрытие =====================

            // Плохо: может вызвать утечки ресурсов
//            browser.close();

            // Правильно: закрываем контексты явно
            for (BrowserContext cont : browser.contexts()) {
                cont.close(); // Сохраняет артефакты (HAR, видео)
            }
            browser.close(); // Теперь безопасно

            // ===================== Сценарий 1: Мобильная эмуляция =====================

            BrowserContext mobileContext = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(375, 812)
                    .setDeviceScaleFactor(3.0)
                    .setIsMobile(true)
                    .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X)"));

            // ===================== Сценарий 2: Работа с геолокацией =====================

            BrowserContext geoContext = browser.newContext(new Browser.NewContextOptions()
                    .setGeolocation(55.755826, 37.617300)); // Москва

            // ===================== Сценарий 3: Авторизация через прокси =====================

            BrowserContext proxyContext = browser.newContext(new Browser.NewContextOptions()
                    .setProxy(new Proxy("http://corp-proxy:3128")
                            .setUsername("employee")
                            .setPassword("pass123")));
        }

        /**
         * Важные предупреждения
         * - Всегда закрывайте контексты явно:
         * browser.close(); // Может потерять данные
         * <br>
         * // Хорошо
         * context.close(); // Сохраняет HAR/видео
         * browser.close();
         * <br>
         * - Осторожно с setJavaScriptEnabled(false):
         * Ломает большинство современных сайтов.
         * Используйте только для специальных тестов
         * <br>
         * - Особенности WebKit:
         * Клиентские сертификаты не работают с localhost
         * .setBaseURL("https://local.playwright")
         * <br>
         * - Режим инкогнито:
         * Каждый newContext() создает изолированное окружение
         * BrowserContext session1 = browser.newContext();
         * BrowserContext session2 = browser.newContext(); // Куки не разделяются
         */
    }
}
