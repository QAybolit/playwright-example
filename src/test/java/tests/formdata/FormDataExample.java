package tests.formdata;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormDataExample {

    public static void main(String[] args) {

        /**
         * FormData используется для создания данных форм при отправке HTTP-запросов через APIRequestContext.
         * Поддерживает текстовые поля, файлы и множественные значения.
         * <br>
         * Особенности метода set:
         * - Перезаписывает существующие значения
         * - Для файлов автоматически определяет MIME-тип
         * Особенности метода append:
         * - Добавляет значения к существующим ключам
         * - Позволяет создавать поля с несколькими значениями
         * <br>
         * При использовании setForm() Playwright автоматически:
         * - Устанавливает Content-Type: multipart/form-data
         * - Генерирует границы (boundary)
         * - Кодирует данные
         * <br>
         * Типы значений: String, boolean, int, Path, FilePayload
         */

        try (Playwright playwright = Playwright.create()) {

            // ===================== Создание экземпляра =====================

            FormData form = FormData.create();

            // ===================== Установка значений (set) =====================

            // Простые значения
            form.set("username", "john_doe")
                    .set("newsletter", true)
                    .set("age", 30);

            // Файл через путь
            form.set("avatar", Paths.get("avatar.jpg"));

            // Виртуальный файл
            form.set("resume", new FilePayload(
                    "CV.pdf",
                    "application/pdf",
                    Files.readAllBytes(Paths.get("my-cv.pdf")))
            );

            // ===================== Добавление значений (append) =====================

            form.append("interests", "reading")
                    .append("interests", "hiking")
                    .append("attachments", Paths.get("file1.txt"))
                    .append("attachments", Paths.get("file2.txt"));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ===================== Полный пример =====================

        try (Playwright playwright = Playwright.create()) {
            // 1. Создание FormData
            FormData form = FormData.create()
                    .set("email", "test@example.com")
                    .set("subscribe", true)
                    .append("languages", "java")
                    .append("languages", "javascript")
                    .set("avatar", Paths.get("user-data/photo.jpg"))
                    .set("customFile", new FilePayload(
                            "config.json",
                            "application/json",
                            "{\"setting\":\"enabled\"}".getBytes()
                    ));

            // 2. Отправка POST-запроса
            APIRequestContext request = playwright.request().newContext();
            APIResponse response = request.post(
                    "https://api.example.com/submit",
                    RequestOptions.create().setForm(form)
            );

            // 3. Проверка результата
            Assertions.assertEquals(200, response.status());
            System.out.println("Ответ сервера: " + response.text());

            // ===================== Регистрация пользователя =====================
            FormData regForm = FormData.create()
                    .set("name", "Alice")
                    .set("email", "alice@example.com")
                    .set("password", "s3cret")
                    .set("avatar", Paths.get("alice.jpg"));

            // ===================== Отправка массива файлов =====================
            FormData multiUpload = FormData.create()
                    .append("docs", Paths.get("doc1.pdf"))
                    .append("docs", Paths.get("doc2.pdf"))
                    .append("docs", Paths.get("doc3.pdf"));

            // ===================== Сложная форма с JSON =====================
            FormData complexForm = FormData.create()
                    .set("user", "{\"name\":\"Bob\",\"age\":25}") // JSON-строка
                    .set("preferences", new FilePayload(
                            "prefs.json",
                            "application/json",
                            Files.readAllBytes(Paths.get("preferences.json")))
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
