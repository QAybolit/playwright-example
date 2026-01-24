package tests.worker;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkerExample {

    /**
     * Worker представляет Web Worker - отдельный поток выполнения JavaScript.
     * Playwright позволяет отслеживать создание и завершение воркеров, взаимодействовать с ними и выполнять код в их контексте.
     * <br>
     * Основные методы:
     * - url()
     * Возвращает URL-адрес скрипта воркера.
     * - evaluate(expression, arg)
     * Выполняет JavaScript в контексте воркера и возвращает результат.
     * - evaluateHandle(expression, arg)
     * Выполняет JavaScript и возвращает результат как JSHandle (для работы с DOM-объектами).
     * - waitForClose(options, callback)
     * Ожидает завершение работы воркера после выполнения действия.
     * - onClose(handler)
     * Срабатывает при завершении работы воркера.
     */

    // ===================== Полный пример тестового класса =====================
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        // Отслеживание создания воркеров
        page.onWorker(worker -> {
            System.out.println("Worker created: " + worker.url());

            // Обработка завершения воркера
            worker.onClose(w ->
                    System.out.println("Worker destroyed: " + w.url()));
        });
    }

    @Test
    void testWebWorker() {
        // 1. Переход на страницу с воркерами
        page.navigate("https://worker-demo.com");

        // 2. Получение активных воркеров
        System.out.println("Active workers:");
        for (Worker worker : page.workers()) {
            System.out.println("- " + worker.url());

            // 3. Выполнение кода в контексте воркера
            String userAgent = (String) worker.evaluate("navigator.userAgent");
            System.out.println("Worker UA: " + userAgent);
        }

        // 4. Инициирование создания нового воркера
        page.click("#start-worker-btn");

        // 5. Ожидание создания воркера
        Worker newWorker = page.waitForWorker(() ->
                page.click("#execute-task-btn")
        );

        // 6. Ожидание завершения воркера
        newWorker.waitForClose(
                new Worker.WaitForCloseOptions().setTimeout(5000),
                new Runnable() {
                    @Override
                    public void run() {
                        page.click("#stop-worker-btn");
                    }
                }
        );
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
