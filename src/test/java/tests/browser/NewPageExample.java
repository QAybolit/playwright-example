package tests.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;

public class NewPageExample {

    public static void main(String[] args) {

        /**
         * Метод newPage - Создает новую страницу в новом контексте браузера. При закрытии страницы контекст автоматически закрывается
         *
         * Используйте только для коротких сценариев или одностраничных приложений. В рабочих проектах явно создавайте
         * контекст через Browser.newContext() и страницы через context.newPage() для контроля времени существования.
         *
         * Основные настройки контекста страницы:
         * - setAcceptDownloads - Разрешает автоматическую загрузку файлов.
         * - setBaseURL - Базовый URL для относительных путей (например, http://localhost:3000).
         * - setBypassCSP - Обход политики безопасности (Content-Security-Policy).
         * - setColorScheme - Эмулирует цветовую схему (prefers-color-scheme).
         * - setDeviceScaleFactor - Коэффициент масштабирования устройства (DPR).
         * - setGeolocation - Геолокация пользователя.
         * - setJavaScriptEnabled - Включает/отключает JavaScript.
         * - setLocale - Локаль для языковых настроек.
         * - setOffline - Эмулирует режим офлайн.
         * - setProxy - Настройки прокси.
         * - setViewportSize - Размер области просмотра (по умолчанию: 1280×720). Используйте null для отключения.
         * - setUserAgent - Кастомный User-Agent.
         *
         * Другие параметры: setExtraHTTPHeaders, setHttpCredentials, setPermissions, setRecordHarPath,
         *  setRecordVideoDir, setStorageState,  setTimezoneId.
         */

        try (Playwright playwright = Playwright.create()) {
            BrowserType chrome = playwright.chromium();
            Browser browser = chrome.launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setTimeout(30_000));

            Page page = browser.newPage();

            // ===================== Методы управления трассировкой (Chromium) =====================

            // startTracing - Запускает трассировку Chromium для отладки.
            browser.startTracing(page, new Browser.StartTracingOptions()
                    .setPath(Paths.get("trace.json")));
            page.navigate("https://www.google.com");
            browser.stopTracing();

            // stopTracing - Останавливает трассировку и возвращает данные:
            byte[] traceData = browser.stopTracing();

            // version() - Возвращает версию браузера:
            String browserVersion = browser.version();

            // ===================== События =====================

            // onDisconnected - Срабатывает при отключении браузера (закрытие приложения, вызов browser.close()):
            browser.onDisconnected(handler -> {
                System.out.println("Браузер отключен");
            });

            /**
             * Важные замечания
             * - Клиентские сертификаты на macOS (WebKit): заменяйте localhost на local.playwright.
             * - Размер видео по умолчанию: 800×450 (можно изменить через setRecordVideoSize).
             * - Режим строгих селекторов (setStrictSelectors: true) гарантирует уникальность элементов.
             */
        }
    }
}
