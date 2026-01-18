package tests.frame;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;
import java.util.List;

public class FrameExample {

    public static void main(String[] args) {

        /**
         * Фреймы — это элементы страницы (например, <iframe>), которые содержат собственные HTML-документы.
         * Playwright предоставляет методы для управления фреймами и их содержимым.
         * Каждая страница имеет главный фрейм (mainFrame()) и дочерние фреймы (childFrames()), образующие иерархическую структуру.
         * <br>
         * События фреймов:
         * - page.onFrameAttached(): фрейм добавлен на страницу.
         * - page.onFrameNavigated(): фрейм перезагрузил URL.
         * - page.onFrameDetached(): фрейм удалён со страницы.
         * <br>
         * Ключевые методы фреймов:
         * - addScriptTag()	Добавляет тег <script> с указанным URL, содержимым или из файла.
         * - addStyleTag()	Добавляет тег <style> или <link> для CSS.
         * - childFrames()	Возвращает список дочерних фреймов.
         * - content()	Возвращает HTML-содержимое фрейма (включая <!DOCTYPE>).
         * - dragAndDrop()	Перетаскивает элемент (source) на другой элемент (target).
         * - evaluate()	Выполняет JavaScript в контексте фрейма и возвращает результат.
         * - evaluateHandle()	Выполняет JavaScript и возвращает результат как JSHandle (для работы с DOM).
         * - frameElement()	Возвращает ElementHandle фрейма (например, <iframe>).
         * <br>
         * Особенности методов:
         * 1. addScriptTag() / addStyleTag():
         * Поддерживают параметры:
         * - setUrl(): загрузка из внешнего источника.
         * - setPath(): загрузка из локального файла.
         * - setContent(): вставка сырого кода.
         * <br>
         * 2. evaluate()
         * - Может возвращать Promise (Playwright автоматически дождётся его выполнения).
         * - Поддерживает передачу ElementHandle в качестве аргумента.
         * <br>
         * 3. dragAndDrop()
         * - Опция setForce(true) игнорирует проверки доступности элемента (например, видимость).
         * - Параметры setSourcePosition() и setTargetPosition() управляют точкой клика.
         * <br>
         * 4. frameElement()
         * - Возвращает элемент <iframe> родительского фрейма, связанный с текущим фреймом.
         */

        // ===================== Примеры использования =====================
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // 1. Навигация на страницу с фреймами
            page.navigate("https://example-with-frames.com");

            // 2. Работа с главным фреймом
            Frame mainFrame = page.mainFrame();

            // Добавление скрипта из файла
            mainFrame.addScriptTag(new Frame.AddScriptTagOptions().setPath(Paths.get("script.js")));

            // Добавление CSS-стилей
            mainFrame.addStyleTag(new Frame.AddStyleTagOptions().setUrl("https://styles.css"));

            // Получение HTML-содержимого
            System.out.println(mainFrame.content());

            // 3. Работа с дочерними фреймами
            List<Frame> childFrames = mainFrame.childFrames();
            Frame firstChild = childFrames.get(0);

            // Выполнение JavaScript во фрейме
            Object result = firstChild.evaluate("() => document.title");
            System.out.println("Child frame title: " + result);

            // Получение элемента <iframe>
            ElementHandle frameElement = firstChild.frameElement();

            // 4. Перетаскивание элемента
            mainFrame.dragAndDrop("#source", "#target",
                    new Frame.DragAndDropOptions().setForce(true));

            // 5. Демонстрация дерева фреймов
            dumpFrameTree(mainFrame, "");

            browser.close();
        }
    }

    // Рекурсивный вывод структуры фреймов
    static void dumpFrameTree(Frame frame, String indent) {
        System.out.println(indent + "Frame URL: " + frame.url());
        for (Frame child : frame.childFrames()) {
            dumpFrameTree(child, indent + "  ");
        }
    }
}
