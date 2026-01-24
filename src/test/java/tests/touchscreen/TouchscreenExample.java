package tests.touchscreen;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Touchscreen;
import org.junit.jupiter.api.Assertions;

public class TouchscreenExample {
    public static void main(String[] args) {

        /**
         * Touchscreen класс для имитации жестов на сенсорных экранах.
         * Работает с пикселями CSS относительно верхнего левого угла области просмотра (viewport).
         * <br>
         * Ключевые особенности
         * - Требует активации опции hasTouch в контексте браузера (BrowserContext context = browser.newContext(new Browser.NewContextOptions().setHasTouch(true));
         * - Работает только в основном фрейме страницы.
         * - Предназначен исключительно для имитации жестов (не для обработки реальных событий касания).
         * <br>
         * Отличие от Page.tap():
         * - Touchscreen.tap() — низкоуровневый метод для ручной имитации.
         * - Page.tap() — высокоуровневый метод, который выбросит ошибку, если hasTouch=false.
         * <br>
         * Методы:
         * tap(x, y)	Имитирует однократное касание (touchstart → touchend)	x, y: координаты в пикселях CSS относительно viewport
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();

            // ===================== Полный пример =====================
            // 1. Активируем поддержку touch
            // Без этой настройки вызов touchscreen.tap() не сработает.
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setHasTouch(true));
            Page page = context.newPage();

            // 2. Загружаем страницу с сенсорным элементом
            page.setContent("""
                        <div id="touch-area" 
                             style="width:200px;height:200px;background:blue;"
                             ontouchend="this.innerText='Tapped!'">
                        </div>
                    """);

            // 3. Получаем объект Touchscreen
            Touchscreen touchscreen = page.touchscreen();

            // 4. Тапаем в центр элемента
            // Генерирует события touchstart и touchend.
            touchscreen.tap(100, 100);

            // 5. Проверяем реакцию
            String result = page.locator("#touch-area").textContent();
            Assertions.assertEquals("Tapped!", result);
        }
    }
}
