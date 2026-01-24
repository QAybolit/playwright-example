package tests.tracing;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import java.nio.file.Paths;

public class TracingExample {

    public static void main(String[] args) {

        /**
         * Трассировка записывает действия браузера и сетевые события. Используется для отладки тестов.
         * <br>
         * Когда использовать:
         * - Для отладки сложных сценариев
         * - Анализ производительности операций
         * - Ограничение: Не записывает тестовые утверждения (assertions)
         * <br>
         * Рекомендация:
         * - Для проектов с Playwright Test используйте встроенную трассировку через конфигурацию
         * - Для чистого Java/Selenium-like сценариев - используйте API Tracing
         * <br>
         * Основные методы
         * - start(options)	Начало записи трассировки
         * - stop([path])	Сохранение трассировки в ZIP
         * - startChunk([options])	Начало нового сегмента трассировки
         * - stopChunk([path])	Сохранение текущего сегмента
         * - group(name, [options])	Группировка операций (логическая)
         * - groupEnd()	Завершение группы
         * <br>
         * Просмотр трассировок
         * npx playwright show-trace path/to/trace.zip
         */

        // ===================== Настройки трассировки =====================
        new Tracing.StartOptions()
                .setScreenshots(true)      // Скриншоты для timeline
                .setSnapshots(true)        // Снимки DOM + сетевая активность
                .setSources(true)          // Исходный код (требует PLAYWRIGHT_JAVA_SRC)
                .setTitle("Checkout Flow") // Название в Trace Viewer
                .setName("trace_prefix");   // Префикс для временных файлов


        // ===================== Полный пример (чистый Java API) =====================
        // Установка путей к исходному коду
        // Переменная окружения PLAYWRIGHT_JAVA_SRC:
        // Формат для Windows: ;-разделитель
        // Формат для Linux/macOS: :-разделитель
        // Должна содержать абсолютные пути к директориям с исходным кодом
        System.setProperty("PLAYWRIGHT_JAVA_SRC",
                Paths.get("src/test/java").toAbsolutePath() + ":" +
                        Paths.get("src/main/java").toAbsolutePath());

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();

            // Настройка трассировки
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true)
                    .setTitle("My Java Trace"));

            Page page = context.newPage();
            page.navigate("https://playwright.dev");

            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("java_trace.zip")));
        }
    }
}
