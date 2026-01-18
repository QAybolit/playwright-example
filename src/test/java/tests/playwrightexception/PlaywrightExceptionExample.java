package tests.playwrightexception;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.impl.TargetClosedError;
import com.microsoft.playwright.options.AriaRole;

import java.util.concurrent.TimeoutException;

public class PlaywrightExceptionExample {

    public static void main(String[] args) {

        /**
         * PlaywrightException - Базовый класс для всех исключений в Playwright. Наследуется от стандартного RuntimeException в Java.
         * Возникает при критических ошибках во время выполнения операций, например:
         * - При закрытии браузера во время выполнения Page.evaluate()
         * - При попытке взаимодействия с уже закрытой страницей
         * - При внутренних ошибках движка Playwright
         * <br>
         * Все специфические исключения Playwright наследуются от этого класса.
         * Содержит информацию об ошибке в читаемом виде.
         * Не требует обязательной обработки (unchecked exception)
         */

        // ===================== Действие с закрытым браузером =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            browser.close(); // Закрываем браузер
            page.navigate("https://example.com"); // Исключение!
        } catch (PlaywrightException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }


        // ===================== Таймаут ожидания элемента =====================
        // page.locator("#missing-element").click(new Locator.ClickOptions().setTimeout(1000));
        // Сообщение:
        // Timeout 1000ms exceeded during Locator.click()


        // ===================== Неудачная навигация =====================
        // page.navigate("https://unreachable-url.example.com");
        // Сообщение:
        // Navigation failed because page was closed!

        /**
         * Иерархия исключений Playwright
         * <br>
         * PlaywrightException
         * ├── TimeoutException       // Истекло время ожидания
         * ├── TargetClosedException  // Цель (страница/браузер) закрыта
         * ├── DownloadFailedException // Ошибка загрузки файла
         * └── ...                    // Другие специфические исключения
         */

        // ===================== Обработка исключений (практический пример) =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            try {
                // 1. Попытка навигации на недоступный URL
                page.navigate("https://this-url-does-not-exist.example");

            } catch (PlaywrightException e) {
                System.out.println("Сетевая ошибка: " + e.getMessage());
            }

            // 2. Попытка взаимодействия с несуществующим элементом
            page.getByRole(AriaRole.BUTTON,
                            new Page.GetByRoleOptions().setName("Unexisting Button"))
                    .click(new Locator.ClickOptions().setTimeout(2000));

            // 3. Попытка использования закрытой страницы
            page.close();
            try {
                page.title(); // Действие с закрытой страницей
            } catch (PlaywrightException e) {
                System.out.println("Страница закрыта: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Критическая ошибка: " + e.getMessage());
        }


        // ===================== Рекомендации по обработке =====================

        // Специфичные исключения:
        //Всегда ловите конкретные типы исключений, где это возможно:
        // try {
        //    // Код с Playwright
        //} catch (TimeoutException e) {
        //    // Обработка таймаута
        //} catch (TargetClosedException e) {
        //    // Обработка закрытого контекста
        //} catch (PlaywrightException e) {
        //    // Общая обработка других ошибок
        //}

        // Логирование:
        //Всегда логируйте сообщение исключения - оно содержит детали ошибки:
//        catch (PlaywrightException e) {
//            System.err.println("Playwright error: " + e.getMessage());
//            // Или: e.printStackTrace();
//        }

        // Повторная попытка:
        //Для неустойчивых операций реализуйте retry-логику:
        // int attempts = 0;
        //while (attempts < 3) {
        //    try {
        //        page.click("#unstable-element");
        //        break;
        //    } catch (TimeoutException e) {
        //        attempts++;
        //        page.reload();
        //    }
        //}


        // Кастомные проверки:
        //Используйте утверждения вместо исключений для проверки состояний:
        // Вместо:
        //try {
        //    page.locator(".success-message").isVisible();
        //} catch (Exception e) { /* ... */ }
        //
        // Лучше:
        //assertThat(page.locator(".success-message")).isVisible();
    }
}
