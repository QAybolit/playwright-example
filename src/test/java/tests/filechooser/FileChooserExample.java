package tests.filechooser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FilePayload;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChooserExample {

    public static void main(String[] args) {

        /**
         * FileChooser позволяет управлять диалогами выбора файлов без GUI.
         * Возникает при клике на <input type="file">.
         * <br>
         * Важные особенности
         * Типы файлов:
         * - Реальные файлы через Paths.get()
         * - Виртуальные файлы через FilePayload
         * - Поддержка массивов для множественной загрузки
         * Не требует явного ожидания - waitForFileChooser блокируется до появления диалога
         * Проверка типа элемента: Assertions.assertEquals("file", chooser.element().getAttribute("type"));
         * Проверка множественного выбора: Assertions.assertTrue(chooser.isMultiple());
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Ожидание диалога =====================

            FileChooser chooser = page.waitForFileChooser(
                    () -> {
                        page.click("#upload-button");
                    }
            );

            // ===================== Загрузка файлов =====================

            // Один файл
            chooser.setFiles(Paths.get("document.pdf"));

            // Несколько файлов
            chooser.setFiles(new Path[]{
                    Paths.get("file1.txt"),
                    Paths.get("file2.jpg")
            });

            // Виртуальные файлы
            FilePayload file = new FilePayload(
                    "test.json",
                    "application/json",
                    "{\"data\":\"value\"}".getBytes()
            );

            // ===================== Информация о диалоге =====================

            ElementHandle input = chooser.element();   // Элемент <input>
            boolean isMultiple = chooser.isMultiple(); // Поддержка множественного выбора
            Page sourcePage = chooser.page();          // Страница-источник
        }

        // ===================== Полный пример 1 =====================

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            page.navigate("https://file-upload-example.com");

            // 1. Ожидание диалога
            FileChooser chooser = page.waitForFileChooser(() -> {
                page.click("#select-file");
            });

            // 2. Проверка типа элемента
            Assertions.assertEquals("file", chooser.element().getAttribute("type"));

            // 3. Загрузка файла
            chooser.setFiles(Paths.get("test-data/photo.jpg"));

            // 4. Проверка загрузки
            String fileName = page.locator("#filename").textContent();
            Assertions.assertEquals("photo.jpg", fileName);
        }

        // ===================== Полный пример 2 =====================

        try (Playwright playwright = Playwright.create()) {
            Page page = playwright.firefox().launch().newPage();
            page.navigate("https://multi-upload.com");

            FileChooser chooser = page.waitForFileChooser(() -> {
                page.click("#attach-files");
            });

            // Проверка поддержки множественного выбора
            Assertions.assertTrue(chooser.isMultiple());

            // Загрузка трёх файлов
            chooser.setFiles(new Path[]{
                    Paths.get("file1.pdf"),
                    Paths.get("file2.xlsx"),
                    Paths.get("file3.png")
            });

            // Проверка количества
            int count = Integer.parseInt(page.locator(".file-count").textContent());
            Assertions.assertEquals(3, count);
        }

        // ===================== Полный пример 3 =====================

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://file-upload-example.com");

            // 1. Ожидание диалога
            FileChooser chooser = page.waitForFileChooser(() -> {
                page.click("#upload-button");
            });

            // 2. Создание виртуального файла
            FilePayload file = new FilePayload(
                    "test.json",
                    "application/json",
                    "{\"data\":\"value\"}".getBytes()
            );

            // 3. Загрузка файла
            chooser.setFiles(file);

            // 4. Проверка результата
            String result = page.locator("#upload-status").textContent();
            Assertions.assertEquals("File uploaded: test.json", result);
        }
    }
}
