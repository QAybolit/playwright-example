package tests.frame;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameLocator2Example {

    public static void main(String[] args) {

        /**
         * Представляет iframe на странице, позволяя находить элементы внутри него.
         * FrameLocator строгий - операции завершатся ошибкой, если селектор соответствует нескольким элементам.
         * Создается через:
         * - contentFrame() (из Locator)
         * - frameLocator() (из Page или Locator)
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Пример создания =====================
            // Из Locator
            Locator frameLoc = page.locator("#my-frame");
            FrameLocator frame1 = frameLoc.contentFrame();

            // Из Page
            FrameLocator frame2 = page.frameLocator(".result-frame").first();


            // ===================== frameLocator(selector)  =====================
            // Создает вложенный FrameLocator для дочернего фрейма.
            FrameLocator nestedFrame = frame1.frameLocator(".nested-iframe");


            // ===================== getByAltText(text[, options])  =====================
            // Находит элемент по альтернативному тексту (например, <img alt="logo">).
            Locator img = frame1.getByAltText("Company Logo");
            img.click();


            // ===================== getByLabel(text[, options])  =====================
            // Находит элемент по тексту связанной метки (<label>) или ARIA-атрибутам.
            Locator emailInput = frame1.getByLabel("Email Address");
            emailInput.fill("test@example.com");


            // ===================== getByPlaceholder(text[, options]) =====================
            // Находит элемент по тексту-плейсхолдеру (<input placeholder="Search">).
            Locator search = frame1.getByPlaceholder("Enter keywords");
            search.fill("Playwright");


            // ===================== getByRole(role[, options]) =====================
            // Находит элемент по ARIA-роли и атрибутам. Поддерживает 60+ ролей (BUTTON, CHECKBOX и др.).
            // Поиск кнопки с именем "Submit"
            Locator submitBtn = frame1.getByRole(AriaRole.BUTTON,
                    new FrameLocator.GetByRoleOptions().setName("Submit"));

            // Поиск чекбокса в отмеченном состоянии
            Locator toggle = frame1.getByRole(AriaRole.CHECKBOX,
                    new FrameLocator.GetByRoleOptions()
                            .setName("Enable Feature")
                            .setChecked(true));


            // ===================== getByTestId(testId) =====================
            // Находит элемент по тестовому идентификатору (по умолчанию data-testid).
            // Важно: Атрибут можно настроить через Selectors.setTestIdAttribute().
            Locator directions = frame1.getByTestId("directions-btn");
            directions.click();


            // ===================== getByText(text[, options]) =====================
            // Находит элемент, содержащий указанный текст (с нормализацией пробелов).
            // Для кнопок (<input type="button">) ищет по value.
            // Игнорирует регистр при поиске подстроки
            // Точное совпадение требует полного соответствия
            // Поиск по подстроке
            Locator worldSpan = frame1.getByText("world");

            // Точное совпадение
            Locator exactDiv = frame1.getByText("Hello", new FrameLocator.GetByTextOptions().setExact(true));

            // Регулярное выражение
            Locator helloDivs = frame1.getByText(Pattern.compile("Hello"));


            // ===================== getByTitle(text[, options]) =====================
            // Находит элемент по атрибуту title.
            Locator issues = frame1.getByTitle("Open issues");
            assertThat(issues).hasText("42");


            // ===================== locator(selectorOrLocator[, options]) =====================
            // Основной метод для поиска элементов внутри фрейма. Поддерживает фильтрацию через has/hasNot.
            // Простой селектор
            Locator submitBtn1 = frame1.locator("button.primary");

            // С фильтрацией
            Locator activeItem = frame1.locator("li",
                    new FrameLocator.LocatorOptions()
                            .setHasText("Active")
                            .setHasNot(frame1.locator(".deleted"))
            );


            // ===================== owner() =====================
            // Возвращает локатор самого iframe (обратное преобразование к contentFrame()).
            FrameLocator frameLoc1 = page.locator("iframe").contentFrame();
            Locator iframeElement = frameLoc1.owner();
            assertThat(iframeElement).isVisible();


            // ===================== Ключевые особенности FrameLocator: =====================

            // Каскадный поиск:
            page.frameLocator(".outer")
                    .frameLocator(".inner")
                    .getByText("Submit")
                    .click();

            // Динамические фильтры:
            frame1.locator("tr",
                    new FrameLocator.LocatorOptions()
                            .setHasText("2024")
                            .setHas(frame1.getByRole(AriaRole.BUTTON))
            );
        }

        // ===================== Пример 1 =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // Переходим на страницу с фреймами
            page.navigate("https://example-test-site.com/frames");

            // 1. Получаем основной фрейм
            FrameLocator mainFrame = page.frameLocator("#main-frame").first();

            // 2. Работа с элементами внутри фрейма
            mainFrame.getByLabel("Username").fill("test_user");
            mainFrame.getByPlaceholder("Password").fill("s3cr3t");

            // 3. Поиск по ARIA-роли
            mainFrame.getByRole(AriaRole.BUTTON,
                    new FrameLocator.GetByRoleOptions()
                            .setName(Pattern.compile("submit", Pattern.CASE_INSENSITIVE))
            ).click();

            // 4. Работа с вложенным фреймом
            FrameLocator nestedFrame = mainFrame.frameLocator(".nested-iframe");
            nestedFrame.getByAltText("Status Icon").click();

            // 5. Проверка состояния элемента
            Locator toggle = nestedFrame.getByRole(AriaRole.CHECKBOX,
                    new FrameLocator.GetByRoleOptions()
                            .setName("Enable Notifications")
                            .setChecked(true));

            if (toggle.isVisible()) {
                System.out.println("Уведомления включены");
            }

            browser.close();
        }

        // ===================== Пример 2 =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://example-test-site.com/complex-frames");

            // Настройка кастомного testid атрибута
            playwright.selectors().setTestIdAttribute("data-qa-id");

            // Работа с главным фреймом
            FrameLocator mainFrame = page.frameLocator(".main-content");

            // 1. Поиск по тестовому ID
            mainFrame.getByTestId("navigation-menu").click();

            // 2. Поиск по тексту с фильтрацией
            Locator activeTab = mainFrame.locator(".tab",
                    new FrameLocator.LocatorOptions()
                            .setHasText("Active Projects")
                            .setHasNot(mainFrame.locator(".archived"))
            );

            // 3. Поиск по заголовку
            Locator helpIcon = mainFrame.getByTitle("Help Center");
            helpIcon.hover();

            // 4. Доступ к вложенному фрейму
            FrameLocator widgetFrame = mainFrame.frameLocator(".widget-frame");

            // 5. Поиск в виджете
            widgetFrame.getByRole(AriaRole.TEXTBOX,
                    new FrameLocator.GetByRoleOptions()
                            .setName("Search features")
            ).fill("Analytics");

            // 6. Получение владельца фрейма
            Locator widgetIframe = widgetFrame.owner();
            System.out.println("ID виджета: " + widgetIframe.getAttribute("id"));

            // 7. Проверка состояния
            assertThat(activeTab).hasClass("active");
            assertThat(widgetIframe).isVisible();

            browser.close();
        }
    }
}
