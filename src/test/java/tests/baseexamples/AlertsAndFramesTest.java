package tests.baseexamples;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import tests.BaseTest;

public class AlertsAndFramesTest extends BaseTest {

    @Test
    public void alertsAndFramesTest() {

        // ===================== 1. Методы работы с iframes в Playwright =====================  

        // Явное переключение на фрейм через объект Frame.
        // Неявное переключение с использованием Locator (рекомендуется в Playwright v1.14+).

        // frameLocator(): Создает объект для поиска внутри iframe.

        // frame.name() / frame.url(): Получение фрейма по атрибуту name или URL.

        // contentFrame(): Доступ к фрейму из элемента (например, <iframe>).


        // =====================  Пример 1: Переключение на iframe по атрибуту name =====================  

        // Открываем тестовую страницу с iframe
        page.navigate("https://example.com/iframe-page");

        // Получаем фрейм по атрибуту name
        Frame iframe = page.frame("widget-frame"); // name="widget-frame"

        // Взаимодействуем с элементом внутри фрейма
        iframe.locator("#submit-button").click(); // Клик по кнопке внутри iframe

        // Возвращаемся к основному контенту
        page.mainFrame(); // Необязательно, т.к. дальнейшие действия автоматически в основном контексте


        // =====================  Пример 2: Работа с фреймом через frameLocator (рекомендуемый способ) =====================  

        page.navigate("https://example.com/login-iframe");

        // Используем FrameLocator для цепочки действий
        page.frameLocator("iframe[title='Login Form']")
                .locator("input#username")
                .fill("test_user"); // Заполняем поле логина

        // Клик по кнопке внутри того же фрейма
        page.frameLocator("iframe[title='Login Form']")
                .locator("button#submit")
                .click();

        // Проверка элемента в основном контексте
        assert page.locator("#welcome-message").isVisible(); // Утверждение вне фрейма


        // =====================  Пример 3: Вложенные iframes и навигация ===================== 

        page.navigate("https://example.com/nested-frames");

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


        // ===================== 1. Методы работы со всплывающими окнами в Playwright =====================  

        /**
         * Нативные диалоги (Browser Dialogs):
         * - Alert: Уведомление с кнопкой OK.
         * - Confirm: Диалог с OK/Cancel. Возвращает true/false.
         * - Prompt: Запрос ввода текста + OK/Cancel.
         */

        // page.onDialog(): Слушатель событий диалогов.
        // dialog.accept(), dialog.dismiss(): Принятие/отклонение.
        // dialog.defaultValue(), dialog.message(): Данные диалога.

        // Автопринятие всех диалогов
        page.onDialog(Dialog::accept);

        // Избирательная обработка
        page.onDialog(dialog -> {
            if ("confirm".equals(dialog.type())) dialog.dismiss();
        });

        // Явное ожидание с помощью waitFor
        page.waitForPopup(() -> page.locator("#trigger-btn").click());

        // ===================== Пример 1: Обработка Alert =====================  

        // Слушатель для автоматического принятия Alert
        page.onDialog(dialog -> {
            System.out.println("Alert text: " + dialog.message());
            dialog.accept(); // Обязательно закрыть диалог
        });

        page.navigate("https://example.com/alert-demo");
        page.locator("button#show-alert").click(); // Триггер Alert

        // Проверка, что после Alert произошел переход
        assert page.url().contains("success");


        // ===================== Пример 2: Работа с Confirm и Prompt =====================  

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


        // ===================== Пример 3: Взаимодействие с DOM-модалкой =====================

        page.navigate("https://example.com/modal-demo");

        // 1. Открытие модалки
        page.locator("button#open-modal").click();

        // 2. Ожидание появления и работа с элементами
        Locator modal = page.locator(".modal-dialog");
        modal.locator("input#email").fill("test@example.com");
        modal.locator("button#submit").click();

        // 3. Проверка исчезновения модалки
        PlaywrightAssertions.assertThat(modal).isHidden(); // Автоматическое ожидание скрытия

        // 4. Альтернатива: закрытие через кнопку
        // modal.locator(".close-btn").click();

    }

}
