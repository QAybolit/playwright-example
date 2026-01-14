package tests.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.Geolocation;
import com.microsoft.playwright.options.HarNotFound;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BrowserContextsExample {

    /**
     * BrowserContexts представляют изолированные сессии браузера, позволяя работать с несколькими независимыми
     * контекстами (например, инкогнито-режим). Страницы, открытые внутри контекста
     * (включая всплывающие окна через window.open), принадлежат этому же контексту.
     * <p>
     * Ключевые возможности
     * - Изолированные сессии: Контексты не сохраняют данные (куки, кеш) на диск. Создаются через browser.newContext().
     * - Автоматическая очистка: При закрытии контекста (context.close()) все связанные страницы завершают работу.
     */

    public static void main(String[] args) {

        try (Playwright playwright = Playwright.create()) {
            BrowserType chrome = playwright.chromium();
            Browser browser = chrome.launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setTimeout(30_000));
            BrowserContext context = browser.newContext();

            // ===================== Управление куками =====================

            // Добавление кук:
            // Параметры:
            // Обязательные: name, value.
            // Опциональные: domain, path, expires (время в Unix-секундах), secure, httpOnly.
            context.addCookies(Arrays.asList(
                    new Cookie("name", "value").setDomain("example.com")
            ));

            // Чтение куки:
            List<Cookie> cookies = context.cookies("https://example.com");

            // Удаление куки:
            context.clearCookies(); // Все куки

            context.clearCookies(new BrowserContext.ClearCookiesOptions() // С фильтром
                    .setName("session-id")
                    .setDomain("my-origin.com")
            );

            // ===================== Запуск скриптов при инициализации =====================

            // Выполняет JavaScript до загрузки страницы (например, для модификации окружения):
            // Файл preload.js: Math.random = () => 42;
            // Важно: Порядок выполнения нескольких скриптов не гарантирован.
            context.addInitScript(Paths.get("preload.js"));

            // ===================== Управление разрешениями =====================

            // Выдача разрешений (например, доступ к геолокации):
            // Поддерживаемые разрешения: "geolocation", "camera", "clipboard-read" и др.
            context.grantPermissions(Arrays.asList("camera", "microphone"));

            // Сброс разрешений:
            context.clearPermissions();

            // ===================== Интеграция с JavaScript =====================

            // Экспорт функций в браузер:
            // Функция sha256() станет доступна в window всех страниц контекста.
            context.exposeFunction("sha256", argumentss -> {
                        String text = (String) argumentss[0];
                        Object[] hash = null;
                        // Логика хеширования...
                        return hash;
                    }
            );

            // ===================== Работа с фоновыми страницами (только Chromium) =====================

            // Получить все фоновые страницы:
            List<Page> backgroundPages = context.backgroundPages();

            // ===================== Создание новой страницы =====================

            // Возвращает: Объект новой вкладки.
            Page page = context.newPage();

            // ===================== Создание сессии CDP (только Chromium) =====================

            // Назначение: Низкоуровневое взаимодействие с браузером через Chrome DevTools Protocol.
            // Параметр: page или frame — целевой элемент.
            CDPSession session = context.newCDPSession(page);

            // ===================== Список открытых страниц =====================

            // Возвращает: Список всех активных вкладок в контексте.
            List<Page> allPages = context.pages();

            // ===================== Перехват сетевых запросов =====================

            // Блокировка изображений
            context.route("**/*.{png,jpg,jpeg}", route -> route.abort());

            // Модификация запросов
            // Параметры:
            // url: Шаблон URL (строка, regex, предикат)
            // handler: Логика обработки запроса
            // options.times: Сколько раз применять правило (по умолчанию — всегда)
            context.route("/api/**", route -> {
                if (route.request().postData().contains("my-string")) {
                    route.fulfill(new Route.FulfillOptions().setBody("mocked-data"));
                } else {
                    route.resume();
                }
            });
            // Особенности
            // Не перехватывает запросы от service workers
            // Приоритет над Page.route()
            // Отключает HTTP-кэш

            // ===================== Воспроизведение из HAR-файла =====================

            context.routeFromHAR(Paths.get("network.har"));
            context.routeFromHAR(Paths.get("updated.har"),
                    new BrowserContext.RouteFromHAROptions()
                            .setUpdate(true) // Обновить файл
                            .setNotFound(HarNotFound.FALLBACK) // Действие при отсутствии запроса
            );

            // ===================== Перехват WebSocket =====================

            // TODO ПРИМЕР ИЗ ДОКИ НЕ РАБОЧИЙ, НУЖНО ПРОВЕРИТЬ
//            context.routeWebSocket("/ws", ws -> {
//                ws.routeSend(message -> {
//                    if ("to-be-blocked".equals(message))
//                        return;
//                    ws.send(message);
//                });
//                ws.connect();
//            });

            // ===================== Таймауты =====================

            context.setDefaultNavigationTimeout(60_000); // 60 сек для навигации
            context.setDefaultTimeout(30_000); // 30 сек для всех операций

            // ===================== Дополнительные HTTP-заголовки =====================

            // Заголовки страницы переопределяют контекстные
            // Порядок заголовков не гарантируется
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer token");
            context.setExtraHTTPHeaders(headers);

            // ===================== Эмуляция геолокации =====================

            context.setGeolocation(new Geolocation(59.95, 30.31667)); // Широта, долгота
            context.setGeolocation(null); // Отключение
            // latitude: -90 до 90
            // longitude: -180 до 180
            // accuracy: Точность (по умолчанию 0)
            // Совет: Используйте с grantPermissions(Arrays.asList("geolocation")).

            // ===================== Эмуляция офлайн-режима =====================

            // Имитирует отсутствие сетевого подключения для всех страниц контекста.
            context.setOffline(true); // Без интернета

            // ===================== Сохранение состояния хранилища =====================

            // Сохранить в файл
            String state = context.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(Paths.get("state.json")));
//                    .setIndexedDB(true)); // Включая IndexedDB

            // Без сохранения на диск
            String jsonState = context.storageState();

            // ===================== Удаление маршрутов =====================

            // Удалить все обработчики для URL
            context.unroute("**/api/*");

            // Удалить конкретный обработчик
            Consumer<Route> handler = route -> route.resume();
            context.unroute("**/images/*", handler);

            // Удалить ВСЕ маршруты (включая HAR)
            context.unrouteAll();

            // ===================== Ожидание условий =====================

            // Ждать, пока в списке не будет 3 ошибок
            List<String> errors = new ArrayList<>();
            context.onResponse(response -> {
                if (!response.ok()) errors.add(response.url());
            });

            context.waitForCondition(() -> errors.size() >= 3);

            // ===================== Ожидание сообщений в консоли =====================

            ConsoleMessage msg = context.waitForConsoleMessage(
                    new BrowserContext.WaitForConsoleMessageOptions()
                            .setPredicate(m -> m.text().contains("Warning")), // Фильтр
                    () -> page.click("button") // Действие, вызывающее сообщение
            );
            System.out.println(msg.text());

        }

        // ===================== Ожидание новой страницы =====================

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();

            // Главная страница
            Page page = context.newPage();
            page.navigate("https://example.com");

            // Ожидаем новую страницу при клике
            Page popup = context.waitForPage(
                    new BrowserContext.WaitForPageOptions()
                            .setPredicate(p -> p.url().contains("/login")),
                    () -> page.click("a[target=_blank]")
            );

            // Ожидаем полную загрузку
            popup.waitForLoadState();

            // Теперь можно работать с загруженной страницей
            System.out.println("Popup URL: " + popup.url());
            System.out.println("Popup title: " + popup.title());

            browser.close();
        }

    }

    /**
     * Важные замечания
     * - Изоляция: Каждый контекст — независимая сессия.
     * - Контекст по умолчанию нельзя закрыть (context.close()).
     * - Для секционированных кук (CHIPS) используйте setPartitionKey().
     * - При закрытии контекста можно указать причину: context.close(new CloseOptions().setReason("Тесты завершены"));
     * - Приоритеты: Настройки страницы > настройки контекста, Page.route() > BrowserContext.route()
     * - HAR: update=true — записывает актуальные данные при закрытии,
     * minimal режим — сохраняет только данные для маршрутизации
     * - setOffline(true) + setHTTPCredentials() = полная эмуляция сетевых сбоев
     *
     * Используйте BrowserContexts для изоляции тестов, управления сессиями и тонкой настройки окружения браузера!
     */
}

