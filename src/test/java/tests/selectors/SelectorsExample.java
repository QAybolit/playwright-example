package tests.selectors;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SelectorsExample {
    public static void main(String[] args) {

        /**
         * Селекторы позволяют создавать и использовать кастомные механизмы поиска элементов.
         * Регистрация селекторов выполняется до создания страницы через playwright.selectors().
         * <br>
         * Ключевые методы
         * - register(name, script, [options])	Регистрирует кастомный механизм селекции
         * - setTestIdAttribute(attributeName)	Задаёт атрибут для getByTestId()
         * <br>
         * Особенности работы
         * 1. Кастомные селекторы:
         * - Движок должен реализовать методы query() и queryAll().
         * - Скрипт выполняется в контексте страницы.
         * - Префикс name используется в локаторах: prefix=selector.
         * 2. Изоляция окружения:
         * - setContentScript(true): ограничивает доступ к JS фрейма.
         * 3. Test ID:
         * - По умолчанию используется data-testid.
         * - setTestIdAttribute() меняет атрибут для всех страниц: playwright.selectors().setTestIdAttribute("data-qa");
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Регистрация селектора по тегу: =====================
            String tagEngineScript =
                    "({ query: (root, selector) => root.querySelector(selector), " +
                            "queryAll: (root, selector) => Array.from(root.querySelectorAll(selector)) })";

            playwright.selectors().register("tag", tagEngineScript);

            // Использование
            Locator button = page.locator("tag=button");


            // ===================== Кастомный test ID: =====================
            playwright.selectors().setTestIdAttribute("data-qa-id");
            Locator element = page.getByTestId("user-profile");


            // ===================== Порядок регистрации: =====================
            // ДО создания страницы!
            playwright.selectors().register("...", "...");
            Browser browser1 = playwright.chromium().launch();


            // ===================== Структура скрипта: =====================
            // query(): возвращает первый совпавший элемент.
            // queryAll(): возвращает все совпавшие элементы (массив).


            // ===================== Динамическая загрузка скрипта: =====================
            Path scriptPath = Paths.get("path/to/engine.js");
            String script = new String(Files.readAllBytes(scriptPath));
            playwright.selectors().register("dynamic", script);

            // ===================== Изоляция скрипта: =====================
//            setContentScript(true) полезен для избежания конфликтов с JS страницы.

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
