package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest extends BaseTest {

    @Test
    public void exampleTest() {
        // ================== 1. НАВИГАЦИЯ И ЯВНОЕ ОЖИДАНИЕ ==================

        page.navigate("https://demoqa.com");

        page.goBack(); // Возврат на предыдущую страницу

        page.goForward(); // Переход на следующую страницу

        page.reload(); // Перезагрузка страницы

        page.reload(new Page.ReloadOptions().setWaitUntil(WaitUntilState.NETWORKIDLE)); // Ждать отсутствия сетевых запросов

        page.waitForSelector(".card", new Page.WaitForSelectorOptions().setTimeout(10000));

        page.waitForSelector("div#results"); // Ожидание появления результатов

        page.waitForNavigation(() -> page.click("a#next")); // Ожидание перехода на следующую страницу

        page.waitForTimeout(2000); // Ожидание 2 секунды

        page.navigate("https://example.com", new Page.NavigateOptions()
                .setTimeout(60000)       // Макс. время ожидания (60 сек)
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED) // Ждать "domcontentloaded" вместо "load"
        );

        Page newTab = context.newPage(); // Создать новую вкладку в текущем контексте
        newTab.navigate("https://new-page.com");

        List<Page> pages = context.pages(); // Получить все вкладки контекста

        Page secondTab = pages.get(1); // Переключиться на вторую вкладку
        secondTab.bringToFront(); // Активировать вкладку

        secondTab.close(); // Закрыть вкладку

        page.setViewportSize(1920, 1080); // Установить размер окна 1920x1080

        page.setViewportSize(375, 812); // Эмуляция мобильного устройства

        page.keyboard().press("F11"); // Активировать полноэкранный режим

        String currentUrl = page.url(); // Получить текущий URL

        String history = page.evaluate("() => window.history.length").toString(); // Получить историю навигации

        page.evaluate("() => window.history.pushState({}, '', '/new-url')"); // Добавить запись в историю (JS)


        // ================== 2. ЛОКАТОРЫ ==================

        Locator elementsCard = page.locator("div.card:has-text('Elements')"); // css селектор
        elementsCard.click();

        page.locator("li.btn-light:has-text('Text Box')").click(); // Поиск по тексту

        Locator fullNameLabel = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Full Name")); // Поиск по роли ARIA (лучшая практика)


        // ================== 3. ВЗАИМОДЕЙСТВИЕ С ЭЛЕМЕНТАМИ ==================

        fullNameLabel.fill("Иван Петров"); // Быстрое заполнение

        Locator emailInput = page.locator("#userEmail");
        emailInput.type("text@example.com"); // Посимвольный ввод, deprecated

        Locator addressArea = page.locator("#currentAddress");
        addressArea.fill("ул. Ленина, д. 21");

        Locator submitButton = page.locator("#submit");
        submitButton.click(); // клик по кнопке

        page.click("button#submit"); // Клик по кнопке

        page.fill("input#search", "Playwright"); // Заполнение поля поиска

        page.type("input#search", "Playwright"); // Посимвольный ввод текста

        page.check("input#agree"); // Выбор чекбокса

        page.selectOption("select#city", "Moscow"); // Выбор города

        page.hover("img#logo"); // Наведение на логотип

        page.focus("input#email"); // Установка фокуса на поле email


        // ================== 4. ПРОВЕРКИ И ПОЛУЧЕНИЕ ДАННЫХ ==================

        page.waitForSelector("#output"); // Ожиданиие появления результата

        Locator nameResult = page.locator("#name"); // Проверка текста
        assertTrue(nameResult.textContent().contains("Иван Петров"), // textContent() или innerText()
                "Неверное имя в результате");


        Locator emailResult = page.locator("#email"); // Проверка атрибута
        assertEquals("text@example.com",
                emailResult.textContent().replace("Email:", "").trim(),
                "Неверный email в результате");

        String text = page.textContent("h1"); // Получение текста заголовка

        String visibleText = page.innerText("div#content"); // Получение видимого текста

        String href = page.getAttribute("a#link", "href"); // Получение ссылки

        String title = page.title(); // Получение заголовка страницы

        String url = page.url(); // Получение текущего URL

        assertThat(page.locator("#submit-button")).isVisible();  // 1. Проверка видимости элемента

        assertThat(page.locator("h1.title")).hasText("Добро пожаловать");  // 2. Проверка текста элемента

        assertThat(page.locator("a#link")).hasAttribute("href", "/home");  // 3. Проверка атрибута

        assertThat(page.locator(".item")).hasCount(3);  // 4. Проверка количества элементов

        assertThat(page.locator("#deleted-element")).isHidden(); // 5. Проверка отсутствия элемента Или .not().isVisible()

        assertThat(page.locator("input#email")).hasValue("user@example.com");  // 6. Проверка значения инпута

        assertThat(page).hasTitle("Главная страница");  // 7. Проверка заголовка страницы

        assertThat(page).hasURL("https://example.com/home");  // 8. Проверка URL


        // ================== 5. РАБОТА С ЧЕКБОКСАМИ И РАДИОКНОПКАМИ ==================

        page.locator("li:has-text('Check Box')").click();

        // Чекбоксы
        Locator homeCheckbox = page.locator("label:has-text('Home') .rct-checkbox");
        homeCheckbox.check();
        assertTrue(homeCheckbox.isChecked(), "Чекбокс Home должен быть выбран");

        // Радио-кнопки
        page.locator("li:has-text('Radio Button')").click();
        Locator impressiveRadio = page.locator("label:has-text('Impressive')");
        impressiveRadio.check();
        assertTrue(impressiveRadio.isChecked(),
                "Радио-кнопка Impressive должна быть выбрана");


        // ================== 6. СКРИНШОТЫ И ВИДЕО ==================

        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshot.png"))); // Скриншот страницы

//        context.newPage().video().saveAs(path): Сохранение видео.


        // ================== 7. JavaScript В БРАУЗЕРЕ ==================

        Object result = page.evaluate("document.title"); // Получение заголовка страницы
        JSHandle handle = page.evaluateHandle("document.body"); // Получение handle для body

//        Handle (дескриптор) — это ссылка на объект в контексте браузера (например, DOM-элемент, функция, результат вычислений).
//        Он не содержит сам объект, а позволяет взаимодействовать с ним через API (например, передавать между вызовами evaluate,
//        вызывать методы, извлекать свойства), избегая копирования данных между браузером и скриптом. Это повышает эффективность
//        работы с динамическими или тяжелыми объектами.



        // ================== РАБОТА С ФРЕЙМАМИ (iframes)  ==================

        // Открываем тестовую страницу с iframe
        page.navigate("https://example.com/iframe-page");

        // Получаем фрейм по атрибуту name
        Frame iframe = page.frame("widget-frame"); // name="widget-frame"

        // Взаимодействуем с элементом внутри фрейма
        iframe.locator("#submit-button").click(); // Клик по кнопке внутри iframe

        // Возвращаемся к основному контенту
        page.mainFrame(); // Необязательно, т.к. дальнейшие действия автоматически в основном контексте


        // Используем FrameLocator для цепочки действий.
        // frameLocator() не переключает глобальный контекст — последующие вызовы page.* остаются в основном DOM
        page.frameLocator("iframe[title='Login Form']")
                .locator("input#username")
                .fill("test_user"); // Заполняем поле логина

        // Клик по кнопке внутри того же фрейма
        page.frameLocator("iframe[title='Login Form']")
                .locator("button#submit")
                .click();

        // Проверка элемента в основном контексте
        assert page.locator("#welcome-message").isVisible(); // Утверждение вне фрейма


        // Вложенные iframes и навигация
        // Доступ к родительскому фрейму
        Frame parentFrame = page.frame("parent-frame"); // name="parent-frame"

        // Доступ к дочернему фрейму через родительский
        Frame childFrame = parentFrame.childFrames().get(0); // Первый дочерний фрейм

        // Альтернатива: поиск по элементу iframe
        ElementHandle iframeElement = parentFrame.querySelector("iframe.child-class");
        Frame childFrame2 = iframeElement.contentFrame();

        // Взаимодействие с элементом во вложенном фрейме
        childFrame.locator(".nested-button").click();

        // Возврат к основному контексту через page
        page.locator("body").press("Escape"); // Пример действия в основном DOM


        // ================== ОБРАБОТКА ВСПЛЫВАЮЩИХ ОКОН (Alerts, Dialogs)  ==================

        // Для нативных диалогов:
        // page.onDialog(): Слушатель событий диалогов.
        // dialog.accept(), dialog.dismiss(): Принятие/отклонение.
        // dialog.defaultValue(), dialog.message(): Данные диалога.

        // Автопринятие всех диалогов
        page.onDialog(Dialog::accept);

        // Избирательная обработка:
        page.onDialog(dialog -> {
            if ("confirm".equals(dialog.type())) dialog.dismiss();
        });


        // Обработка Confirm
        page.onceDialog(dialog -> {
            if (dialog.type().equals("confirm")) {
                System.out.println("Confirm dialog detected");
                dialog.dismiss(); // Нажимаем "Cancel"
            }
        });
        page.locator("button#delete-item").click();
        assert page.locator("#status").textContent().contains("Отменено");


        // Обработка Prompt
        page.onceDialog(dialog -> {
            if (dialog.type().equals("prompt")) {
                dialog.accept("Playwright"); // Ввод текста + OK
            }
        });
        page.locator("button#ask-name").click();
        assert page.locator("#username").textContent().equals("Playwright");


        // ================== ПРИМЕР 1: Регистрация пользователя (форма) ==================

        // Находим элементы
        Locator emailField = page.getByLabel("Email");
        Locator passwordField = page.locator("#password");
        Locator newsletterCheckbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Subscribe"));
        Locator countrySelect = page.locator("select#country");
        Locator submitBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));

        // Заполняем форму
        emailField.fill("test@example.com");      // Быстрый ввод
        passwordField.type("Qwerty123!");         // Имитация печати (для проверки маски пароля)
        newsletterCheckbox.check();               // Ставим галочку
        countrySelect.selectOption("Germany");    // Выбираем страну

        // Проверяем, что кнопка активна
        if (submitButton.isEnabled()) {
            submitButton.click();
        } else {
            throw new RuntimeException("Кнопка регистрации недоступна!");
        }


        // ================== ПРИМЕР 2: Работа с динамическим UI ==================

        // Наводим мышь на меню для открытия подсказки
        Locator userMenu = page.locator(".user-avatar");
        userMenu.hover();

        // Проверяем, что тултип стал видимым
        Locator tooltip = page.getByText("Профиль пользователя");
        if (tooltip.isVisible()) {
            System.out.println("Тултип отображается корректно");
        }

        // Двойной клик по тексту для его выделения
        Locator documentText = page.locator(".document-content");
        documentText.dblclick();

        // Проверяем, что текст выделен (через CSS-класс)
        if (documentText.getAttribute("class").contains("selected")) {
            System.out.println("Текст выделен!");
        }


        // ================== ПРИМЕР 3: Продвинутая клавиатурная навигация ==================

        // Переходим к полю поиска и вводим запрос
        Locator searchInput = page.getByPlaceholder("Поиск");
        searchInput.focus();
        searchInput.press("Control+A"); // Выделяем существующий текст
        searchInput.press("Backspace"); // Удаляем его
        searchInput.type("Playwright"); // Печатаем новый запрос

        // Открываем фильтры через комбинацию клавиш
        page.keyboard().press("Control+F");

        // Выбираем опцию "Дата" в выпадающем меню
        Locator dateFilter = page.getByText("Сортировка по дате");
        dateFilter.click();

        // Нажимаем Enter для применения фильтра
        page.keyboard().press("Enter");


        // ================== ПРИМЕР 4: Ожидание загрузки данных после клика ==================

        // Кликаем кнопку "Загрузить отчет"
        Locator loadReportButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Загрузить"));
        loadReportButton.click();

        // Ждем исчезновения индикатора загрузки (макс 15 сек)
        page.waitForSelector("#loading-spinner", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(15000)
        );

        // Проверяем заголовок отчета
        Locator reportHeader = page.getByText("Финансовый отчет 2023");
        reportHeader.isVisible();


        // ================== ПРИМЕР 5: Ожидание AJAX-контента ==================

        // Вводим запрос в поиск
        Locator searchInputField = page.getByPlaceholder("Поиск пользователей");
        searchInputField.fill("Alex");

        // Ждем появления результатов (через кастомное условие)
        page.waitForFunction(
                "() => { " +
                        "  const items = document.querySelectorAll('.user-result'); " +
                        "  return items.length > 0 && items[0].innerText.includes('Alex'); " +
                        "}",
                new Page.WaitForFunctionOptions().setTimeout(10000)
        );

        // Кликаем первый результат
        Locator firstResult = page.locator(".user-result").first();
        firstResult.click();


        // ================== ПРИМЕР 6: Ожидание сложного UI-состояния ==================

        // Перетаскиваем элемент в корзину
        Locator file = page.locator(".file-item");
        Locator trashBin = page.locator("#trash-bin");
        file.dragTo(trashBin);

        // Ждем анимации удаления (элемент исчезает из DOM)
        page.waitForSelector(".file-item", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.DETACHED) // Элемент удален из DOM
        );

        // Ждем появления уведомления
        Locator notification = page.getByText("Файл удален");
        notification.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000)
        );

        // Ждем, пока уведомление само скроется через 3 сек
        page.waitForFunction(
                "() => document.querySelector('.notification') === null",
                new Page.WaitForFunctionOptions().setTimeout(4000)
        );


        // ================== Пример 7: Навигация с проверками ==================

        // Открываем главную страницу
        page.navigate("https://shop.example.com", new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.NETWORKIDLE) // Ждем завершения сетевых запросов
        );

        // Проверяем заголовок
        String pageTitle = page.title();
        if (!pageTitle.contains("Магазин")) {
            throw new RuntimeException("Некорректная страница: " + title);
        }

        // Переходим в раздел "Акции"
        page.click("a[href='/sale']");

        // Ждем загрузки нового URL
        page.waitForURL("**/sale");

        // Возвращаемся на главную
        page.goBack();


        // ================== Пример 8: Работа с вкладками ==================

        // Открываем главную страницу
        Page mainPage = context.newPage();
        mainPage.navigate("https://example.com");

        // Открываем ссылку в новой вкладке
        Page popup = context.waitForPage(() -> {
            mainPage.click("a[target='_blank']"); // Клик по ссылке с target="_blank"
        });

        // Активируем новую вкладку
        popup.bringToFront();

        // Логинимся в попапе
        popup.fill("#login", "user");
        popup.fill("#password", "pass");
        popup.click("#submit");

        // Закрываем попап и возвращаемся на главную
        popup.close();
        mainPage.bringToFront();


        // ================== Пример 9: Эмуляция мобильного устройства ==================

        // Создаем контекст с параметрами iPhone 12
        BrowserContext mobileContext = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(390, 844) // Разрешение iPhone 12
                .setDeviceScaleFactor(3.0) // Ретина-дисплей
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)")
        );

        Page mobilePage = mobileContext.newPage();
        mobilePage.navigate("https://m.example.com");

        // Проверяем мобильную версию
        Locator mobileMenu = mobilePage.locator(".hamburger-menu");
        if (!mobileMenu.isVisible()) {
            throw new RuntimeException("Мобильное меню не отображается!");
        }

        // Сделать скриншот для отчета
        mobilePage.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("mobile-view.png")));
    }
}
