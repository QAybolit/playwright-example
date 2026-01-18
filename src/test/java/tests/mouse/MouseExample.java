package tests.mouse;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.MouseButton;

public class MouseExample {

    public static void main(String[] args) {

        /**
         * Mouse - Предоставляет API для управления виртуальной мышью.
         * Работает в координатах CSS основного фрейма относительно верхнего левого угла области просмотра (viewport).
         * Каждая страница (Page) имеет собственную мышь, доступную через page.mouse().
         * <br>
         * Особенности:
         * - Координаты (0,0) - верхний левый угол viewport
         * - Поддерживает эмуляцию промежуточных событий (steps)
         * - Позволяет точно контролировать параметры кликов (кнопка, количество, задержка)
         * - Интегрируется с системой трассировки Playwright (отображается как красная точка)
         * <br>
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://example-test-site.com/canvas-editor");

            // Получаем доступ к мыши
            Mouse mouse = page.mouse();
            Keyboard keyboard = page.keyboard();

            // ===================== click(x, y[, options]) =====================
            // Выполняет полный цикл клика: перемещение → нажатие → отпускание.
            // Эквивалент: move() + down() + up()
            mouse.click(100, 200); // Клик левой кнопкой
            mouse.click(150, 250, new Mouse.ClickOptions()
                    .setButton(MouseButton.RIGHT)
                    .setDelay(100)); // Правый клик с задержкой


            // ===================== dblclick(x, y[, options]) =====================
            // Выполняет двойной клик.
            // Эквивалент: move() + down() + up() + down() + up()
            mouse.dblclick(300, 400); // Двойной клик


            // ===================== down([options]) =====================
            // Генерирует событие нажатия кнопки мыши (mousedown).
            mouse.move(50, 50);
            mouse.down(); // Нажали левую кнопку
            mouse.move(100, 100);
            mouse.up(); // Отпустили


            // ===================== move(x, y[, options]) =====================
            // Перемещает курсор мыши. Может эмулировать промежуточные события.
            // Плавное перемещение с 10 промежуточными событиями
            mouse.move(200, 300, new Mouse.MoveOptions().setSteps(10));


            // ===================== up([options]) =====================
            // Генерирует событие отпускания кнопки мыши (mouseup).
            mouse.down();
            // ... действия ...
            mouse.up(); // Отпустить кнопку


            // ===================== wheel(deltaX, deltaY)  =====================
            // Генерирует событие прокрутки колеса (wheel).
            // Примечание: Для стандартной прокрутки предпочтительнее page.evaluate() или locator.scrollIntoViewIfNeeded().
            // Прокрутка вниз на 500px
            mouse.wheel(0, 500);


            // ===================== Трассировка действий: =====================
            // При запуске с --tracing действия мыши визуализируются как красные точки:
            // mvn test -Dplaywright.trace=on


            // ===================== Плавные движения: =====================
            // Для реалистичной эмуляции используйте промежуточные события:
            mouse.move(500, 500, new Mouse.MoveOptions().setSteps(20));


            // ===================== Комбинация с клавиатурой: =====================
            // Эмуляция сложных взаимодействий:
            mouse.move(400, 400);
            keyboard.down("Shift");
            mouse.down();
            mouse.move(500, 500);
            mouse.up();
            keyboard.up("Shift");


            // ===================== Альтернатива прокрутке: =====================
            // Вместо wheel() лучше использовать:
            page.evaluate("window.scrollBy(0, 500)");
            // или
            page.getByText("Submit").scrollIntoViewIfNeeded();
        }

        // ===================== Пример  =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://example-test-site.com/canvas-editor");

            // Получаем доступ к мыши
            Mouse mouse = page.mouse();

            // 1. Рисование квадрата
            mouse.move(100, 100);
            mouse.down();
            mouse.move(100, 200);
            mouse.move(200, 200);
            mouse.move(200, 100);
            mouse.move(100, 100); // Завершаем квадрат
            mouse.up();

            // 2. Правый клик для открытия меню
            mouse.click(150, 150, new Mouse.ClickOptions()
                    .setButton(MouseButton.RIGHT)
                    .setDelay(50));

            // 3. Выбор инструмента из меню
            mouse.click(170, 170); // Клик по пункту "Круг"

            // 4. Рисование круга
            mouse.move(300, 300);
            mouse.down();
            // Эмулируем плавное движение
            for (int i = 0; i < 10; i++) {
                mouse.move(300 + i*20, 300, new Mouse.MoveOptions().setSteps(5));
            }
            mouse.up();

            // 5. Прокрутка вниз
            mouse.wheel(0, 800);

            // 6. Сохранение результата
            mouse.click(50, 30); // Клик по кнопке "Save"

            // Ожидаем уведомление
            page.waitForSelector(".save-confirmation");

            browser.close();
        }
    }
}
