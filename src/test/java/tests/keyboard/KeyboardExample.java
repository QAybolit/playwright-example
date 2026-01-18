package tests.keyboard;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;

public class KeyboardExample {

    public static void main(String[] args) {

        /**
         * Keyboard - Предоставляет API для управления виртуальной клавиатурой.
         * Позволяет генерировать события нажатия клавиш (keydown), отпускания (keyup) и ввода текста.
         * Поддерживает:
         * - Одиночные нажатия клавиш (press())
         * - Ввод текста с автоматической генерацией событий (type())
         * - Точное управление модификаторами (down(), up())
         * - Прямую вставку текста без событий клавиш (insertText())
         * <br>
         * Ключевые особенности:
         * - Поддерживает 100+ клавиш (F1-F12, ArrowLeft, Digit0-Digit9 и др.)
         * - Работает с модификаторами (Shift, Control, Alt, Meta)
         * - Позволяет эмулировать поведение пользователя (задержки между нажатиями)
         * - Генерирует правильные события для разных платформ (ControlOrMeta)
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://example-test-site.com/text-editor");
            page.click("#editor");
            Keyboard keyboard = page.keyboard();

            // ===================== down(key) =====================
            // Генерирует событие нажатия клавиши (keydown).
            // Для модификаторов (Shift, Ctrl) влияет на последующие операции
            // Повторные вызовы устанавливают флаг repeat: true
            // Поддерживаемые ключи:F1-F12, Digit0-Digit9, KeyA-KeyZ, ArrowLeft, Backspace, ControlOrMeta и др.
            keyboard.down("Shift"); // Нажать Shift


            // ===================== insertText(text) =====================
            // Вставляет текст, генерируя только событие input.
            // Не генерирует keydown/keyup
            // Игнорирует модификаторы
            // Оптимален для сложных символов (эмодзи, иероглифы)
            keyboard.insertText("Привет!");


            // ===================== press(key[, options]) =====================
            // Выполняет полный цикл нажатия (down + up).
            // Рекомендация: Для элементов предпочтительнее Locator.press().
            // Одиночная клавиша
            keyboard.press("Enter");

            // Комбинация клавиш
            keyboard.press("ControlOrMeta+A");

            // С задержкой
            keyboard.press("Shift", new Keyboard.PressOptions().setDelay(100));


            // ===================== type(text[, options]) =====================
            // Этот метод устарел. Для заполнения полей используйте Locator.fill(),
            // для симуляции печати - Locator.pressSequentially().
            // Имитирует посимвольный ввод с генерацией событий.
            // Не активирует модификаторы
            // Для не-US символов - только событие input.
            // Поддерживает задержки между символами
            // Быстрый ввод
            keyboard.type("Hello");

            // Медленный ввод (как пользователь)
            keyboard.type("World", new Keyboard.TypeOptions().setDelay(100));


            // ===================== up(key) =====================
            // Генерирует событие отпускания клавиши (keyup).
            // Используется после down(key) для завершения цикла нажатия.
            // Требует точного соответствия названия клавиши с down(key)
            // Не влияет на физическую клавиатуру (только эмуляция в браузере)
            // Обязателен для "чистки" состояния модификаторов (Shift, Ctrl и др.)
            keyboard.down("Shift");  // Нажали Shift
            keyboard.press("KeyA");  // Нажали A с Shift (получится 'A')
            keyboard.up("Shift");    // Отпустили Shift

            // Выделение текста с помощью Shift + стрелок
            keyboard.down("Shift");
            for (int i = 0; i < 5; i++) {
                keyboard.press("ArrowRight");
            }
            keyboard.up("Shift");  // Важно: отпускаем модификатор!
            keyboard.press("Delete");


            // ===================== Пример 1: Выделение и удаление текста =====================
            keyboard.type("Hello World!");
            keyboard.press("ArrowLeft");
            keyboard.down("Shift");
            for (int i = 0; i < " World".length(); i++) {
                keyboard.press("ArrowLeft");
            }
            keyboard.up("Shift");
            keyboard.press("Backspace");
            // Результат: "Hello!"


            // ===================== Пример 2: Ввод спецсимволов =====================
            // Ввод смайлика (только input событие)
            keyboard.insertText("😊");

            // Ввод китайских иероглифов
            keyboard.type("你好", new Keyboard.TypeOptions().setDelay(50));


            // ===================== Пример 3: Горячие клавиши =====================
            // Сохранение (Ctrl+S / Cmd+S)
            keyboard.press("ControlOrMeta+S");

            // Новое окно (Ctrl+N / Cmd+N)
            keyboard.press("ControlOrMeta+N");

        }

        // ===================== Общий пример =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Переходим на тестовую страницу
            page.navigate("https://example-test-site.com/text-editor");

            // Активируем текстовое поле
            page.click("#editor");

            // Получаем доступ к клавиатуре
            Keyboard keyboard = page.keyboard();

            // 1. Ввод текста разными способами
            keyboard.type("Hello "); // Обычный ввод
            keyboard.insertText("World"); // Прямая вставка
            keyboard.press("!"); // Спецсимвол

            // 2. Форматирование текста
            // Выделяем "World!"
            keyboard.down("Shift");
            for (int i = 0; i < "World!".length(); i++) {
                keyboard.press("ArrowLeft");
            }
            keyboard.up("Shift");

            // Делаем выделенный текст жирным
            keyboard.press("ControlOrMeta+B");

            // 3. Навигация по тексту
            keyboard.press("Home"); // В начало строки
            keyboard.type("Awesome "); // Добавляем слово

            // 4. Работа с блоками
            keyboard.press("Enter"); // Новая строка
            keyboard.press("Tab"); // Отступ
            keyboard.type("- List item");

            // 5. Сохранение документа
            keyboard.press("ControlOrMeta+S");

            // Ожидаем уведомление о сохранении
            page.waitForSelector(".save-notification");

            // 6. Скриншот результата
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("text-editor-result.png")));

            browser.close();
        }
    }
}
