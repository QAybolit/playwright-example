package tests.frame;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import java.util.regex.Pattern;

public class FrameLocatorExample {

    public static void main(String[] args) {

        /**
         * Локаторы — это мощные инструменты для поиска элементов на странице.
         * Playwright предлагает несколько специализированных методов для точного поиска элементов по различным атрибутам.
         * <br>
         * Специализированные методы поиска элементов
         * - getByAltText()	Поиск по alt-тексту (изображения). Учитывает регистр (опционально)
         * - getByLabel()	Поиск по тексту метки. Работает с <label> и aria-атрибутами
         * - getByPlaceholder()	Поиск по тексту-заполнителю. Поддерживает точное совпадение
         * - getByRole()	Поиск по ARIA-роли и имени. 40+ ролей, фильтрация по атрибутам
         * - getByTestId()	Поиск по data-testid атрибуту. Настраиваемый атрибут
         * - getByText()	Поиск по текстовому содержимому. Нормализует пробелы
         */

        /**
         * FrameLocator - основной выбор в большинстве случаев.
         * Используйте FrameLocator, когда:
         * - Локация элементов внутри фрейма - для поиска элементов внутри фрейма
         * - Последовательные операции - когда нужно выполнить несколько действий с элементами фрейма
         * - Более читаемый и безопасный код - рекомендуется для большинства сценариев
         * <br>
         * Frame - для низкоуровневых операций.
         * Используйте Frame, когда:
         * - Нужен прямой доступ к объекту Frame для специфических операций
         * - Работа с событиями фрейма
         * - Получение информации о фрейме
         * - Переключение контекста выполнения
         */

        /**
         * Ожидания waitForFunction()
         * Ожидает, когда JavaScript-выражение вернёт true, и возвращает результат.
         * Используется для отслеживания изменений (например, размеров окна).
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://example-with-frames.com");
            Frame frame = page.mainFrame();

            // ===================== FrameLocator: Работа с элементами внутри фреймов =====================
            // Позволяет искать элементы внутри iframe:
            // Создает контекст внутри указанного фрейма
            // Все последующие поиски выполняются внутри этого фрейма
            Locator submitButton = frame.frameLocator("#my-iframe").getByText("Submit");
            submitButton.click();


            // ===================== Точное совпадение (exact) =====================
            // Для всех методов доступна опция setExact(true):
            frame.getByText("Hello", new Frame.GetByTextOptions().setExact(true));


            // ===================== Поддержка регулярных выражений =====================
            // Все текстовые параметры принимают String или Pattern:
            frame.getByText(Pattern.compile("^hello$", Pattern.CASE_INSENSITIVE));


            // ===================== getByRole: Расширенные фильтры =====================
            // Дополнительные параметры для ролей:
            frame.getByRole(AriaRole.CHECKBOX, new Frame.GetByRoleOptions()
                    .setName("Subscribe")
                    .setChecked(false)
                    .setDisabled(true));


            // ===================== Нормализация текста =====================
            // При поиске по тексту:
            // Множественные пробелы → один пробел
            // Переносы строк → пробелы
            // Игнорирование начальных/конечных пробелов


            // ===================== locator() с фильтрами =====================
            // Позволяет создавать сложные локаторы:
            // Элемент, содержащий другой элемент
            frame.locator("div", new Frame.LocatorOptions()
                    .setHas(frame.locator(".priority-high")));

            // Элемент без определенного текста
            frame.locator("li", new Frame.LocatorOptions()
                    .setHasNotText("Deleted"));


            // ===================== navigate() - контроль ожидания =====================
            // Параметры управления навигацией:
            frame.navigate("any_url", new Frame.NavigateOptions()
                    .setWaitUntil(WaitUntilState.NETWORKIDLE)
                    .setTimeout(60000));


            // ===================== setContent() - загрузка контента =====================
            // Установка HTML с обработкой событий:
            frame.setContent("<html>...</html>", new Frame.SetContentOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));


            // ===================== waitForFunction() =====================
            // Ожидает, когда JavaScript-выражение вернёт true, и возвращает результат.
            // Используется для отслеживания изменений (например, размеров окна).
            // Простой пример
            frame.waitForFunction("window.innerWidth < 100");

            // С передачей аргумента
            String selector = ".foo";
            frame.waitForFunction("selector => !!document.querySelector(selector)", selector);


            // ===================== waitForLoadState() =====================
            // Ожидает достижения фреймом указанного состояния загрузки (load по умолчанию).
            // Playwright автоматически ожидает завершения навигации при действиях (например, click()).
            // Используйте метод, только если нужен контроль над состоянием.
            frame.click("button"); // Вызывает навигацию
            frame.waitForLoadState(LoadState.DOMCONTENTLOADED);


            // ===================== waitForURL() =====================
            // Ожидает перехода фрейма на указанный URL. Поддерживает шаблоны, regex и предикаты.
            frame.click("a.delayed-navigation");
            frame.waitForURL("**/target.html");

        }

        // ===================== Полный пример тестового класса 1 =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // Тестовая страница с фреймами и элементами
            page.navigate("https://example-test-site.com");

            // 1. Работа с фреймом
            FrameLocator frameLocator = page.frameLocator("#user-frame");
            frameLocator.getByLabel("Username").fill("test_user");
            frameLocator.getByPlaceholder("Enter password").fill("secret123");
            frameLocator.getByRole(AriaRole.BUTTON,
                    new FrameLocator.GetByRoleOptions().setName("Login")).click();

            // 2. Поиск по изображению
            frameLocator.getByAltText("Company Logo").click();

            // 3. Поиск по роли с фильтрами
            frameLocator.getByRole(AriaRole.CHECKBOX, new FrameLocator.GetByRoleOptions()
                                    .setName("Accept terms")
                                    .setChecked(false))
                    .check();

            // 4. Поиск по тестовому ID
            frameLocator.getByTestId("submit-button").click();

            // 5. Поиск по тексту (точное совпадение)
            Locator status = frameLocator.getByText("Operation successful",
                    new FrameLocator.GetByTextOptions().setExact(true));

            // Проверка видимости элемента
            if (status.isVisible()) {
                System.out.println("Test passed!");
            }

            // 6. Поиск по регулярному выражению
            Locator partialText = frameLocator.getByText(Pattern.compile("success"));

            browser.close();
        }

        // ===================== Полный пример тестового класса 2 =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // 1. Навигация на страницу с фреймами
            page.navigate("https://example-with-frames.com");

            // 2. Работа с главным фреймом
            Frame mainFrame = page.mainFrame();
            System.out.println("Main frame name: " + mainFrame.name());

            // 3. Проверка состояния фрейма
            if (!mainFrame.isDetached()) {
                System.out.println("Main frame is attached");
            }

            // 4. Навигация внутри фрейма
            Response response = mainFrame.navigate(
                    "https://new-location.com",
                    new Frame.NavigateOptions()
                            .setTimeout(45000)
            );
            System.out.println("Navigation status: " + response.status());

            // 5. Установка контента
            mainFrame.setContent(
                    "<html><body><h1>Dynamic Content</h1></body></html>",
                    new Frame.SetContentOptions()
                            .setWaitUntil(WaitUntilState.LOAD)
            );

            // 6. Работа с дочерним фреймом
            Frame childFrame = mainFrame.childFrames().get(0);

            // Проверка активности элемента
            boolean isEnabled = childFrame.isEnabled("#submit-btn",
                    new Frame.IsEnabledOptions().setTimeout(10000));

            if (isEnabled) {
                // Создание локатора с фильтром
                Locator item = childFrame.locator(".list-item",
                        new Frame.LocatorOptions()
                                .setHas(childFrame.getByText("Important"))
                                .setHasNotText("Deleted")
                );
                item.click();
            }

            // 7. Поиск по title
            childFrame.getByTitle("Item details",
                    new Frame.GetByTitleOptions().setExact(true)).hover();

            // 8. Получение родительских элементов
            System.out.println("Parent page title: " + childFrame.page().title());
            System.out.println("Parent frame URL: " + childFrame.parentFrame().url());

            browser.close();
        }
    }
}
