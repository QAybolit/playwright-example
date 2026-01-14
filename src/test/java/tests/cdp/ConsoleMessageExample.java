package tests.cdp;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleMessageExample {

    public static void main(String[] args) {

        /**
         * ConsoleMessage представляет сообщение, выведенное в консоль браузера
         * (через console.log, console.error и др.). Эти сообщения можно перехватывать и анализировать в тестах.
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Перехват всех сообщений =====================

            page.onConsoleMessage(msg -> {
                System.out.println("[" + msg.type().toUpperCase() + "] " + msg.text());
            });

            // Вывод:
            // [LOG] Страница загружена
            // [ERROR] Не удалось загрузить ресурс


            // ===================== Фильтрация ошибок =====================

            page.onConsoleMessage(msg -> {
                if ("error".equals(msg.type())) {
                    System.out.println("ОШИБКА: " + msg.text() + " в " + msg.location());
                }
            });

            // ===================== Анализ аргументов console.log =====================

            page.onConsoleMessage(msg -> {
                if ("log".equals(msg.type())) {
                    List<JSHandle> args1 = msg.args();
                    System.out.println("Лог с " + args1.size() + " аргументами:");
                    for (JSHandle arg : args1) {
                        System.out.println("  - " + arg.jsonValue());
                    }
                }
            });

            // В браузере: console.log("User:", {name: "John"}, [1, 2, 3])
            // Вывод:
            // Лог с 3 аргументами:
            //   - User:
            //   - {name=John}
            //   - [1, 2, 3]


            // ===================== Ожидание конкретного сообщения =====================

            ConsoleMessage msg = page.waitForConsoleMessage(
                    new Page.WaitForConsoleMessageOptions()
                            .setPredicate(m -> m.text().contains("завершена")),
                    () -> { page.evaluate("console.warn('Проверка завершена')");}
            );

            System.out.println("Получено: " + msg.text()); // "Проверка завершена"

            // Текст сообщения (text)
            // Возвращает текст сообщения (например, "Error: Invalid input").
            String message = msg.text();

            //  Тип сообщения (type)
            // Возвращаемые значения:
            // log, debug, info, error, warning
            // dir, dirxml, table, trace
            // clear, startGroup, endGroup, assert
            // profile, count, timeEnd
            String type = msg.type();

            // Аргументы (args)
            List<JSHandle> arguments = msg.args();

            // Источник (location)
            // Возвращает URL источника в формате: "https://example.com:22:15" (URL:строка:колонка).
            String source = msg.location();

            // Страница (page)
            // Возвращает страницу, сгенерировавшую сообщение (доступно с v1.34).
            Page sourcePage = msg.page();


            // ===================== Типичные сценарии =====================

            // 1. Валидация ошибок:
            // Проверка отсутствия ошибок в консоли
            List<ConsoleMessage> errors = new ArrayList<>();
            page.onConsoleMessage(m -> {
                if ("error".equals(m.type())) errors.add(m);
            });

            // ... выполнение действий ...
            assert errors.isEmpty() : "Найдены ошибки в консоли";

            // 2. Отладка сложных сценариев:
            // Логирование всех сообщений в файл
            page.onConsoleMessage(m -> {
                try (FileWriter writer = new FileWriter("console.log", true)) {
                    writer.write(m.type() + ": " + m.text() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }
}
