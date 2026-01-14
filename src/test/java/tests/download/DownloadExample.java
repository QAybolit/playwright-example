package tests.download;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DownloadExample {

    public static void main(String[] args) {

        /**
         * Download представляет процесс загрузки файла в браузере.
         * Загрузки автоматически удаляются при закрытии браузера.
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Ожидание загрузки =====================

            Download download = page.waitForDownload(() -> {
                page.click("#download-button");
            });

            // ===================== Сохранение файла =====================

            Path savePath = Paths.get("/downloads/", download.suggestedFilename());
            download.saveAs(savePath);

            // ===================== Информация о загрузке =====================

            String url = download.url();               // URL источника
            String filename = download.suggestedFilename(); // Имя файла (из Content-Disposition)
            Page sourcePage = download.page();         // Страница-источник

            // ===================== Управление загрузкой =====================

            // Отмена загрузки
            download.cancel();

            // Удаление файла после загрузки
            download.delete();

            // Проверка ошибок
            String error = download.failure(); // null если успешно

            // ===================== Потоковое чтение =====================

            try (InputStream stream = download.createReadStream()) {
                Files.copy(stream, Paths.get("file.zip"));
            } catch (IOException e) {
                // Обработка ошибок
            }

            // ===================== Путь к временному файлу =====================

            // Имя файла — случайный GUID
            // Доступно только при локальном запуске
            Path tempPath = download.path(); // Путь до завершения загрузки

            // ===================== Проверка загрузки PDF =====================

            Download pdfDownload = page.waitForDownload(() -> {
                page.click("#generate-report");
            });
            pdfDownload.saveAs(Paths.get("reports/report.pdf"));
            Assertions.assertTrue(pdfDownload.suggestedFilename().endsWith(".pdf"));


            // ===================== Массовая загрузка =====================

            List<Download> downloads = new ArrayList<>();
            page.onDownload(downloads::add);

            page.click("#download-all");

            // Ожидание 3 загрузок
            page.waitForCondition(() -> downloads.size() >= 3);

            for (Download d : downloads) {
                d.saveAs(Paths.get("archive/", d.suggestedFilename()));
            }


            // ===================== Потоковая обработка =====================

            Download csvDownload = page.waitForDownload(() -> {
                page.click("#export-csv");
            });

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(csvDownload.createReadStream()))
            ) {
                String header = reader.readLine();
                Assertions.assertTrue(header.contains("Date,Amount"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        // ===================== Полный пример =====================

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();

            // Разрешить загрузки
            context.setDefaultNavigationTimeout(60_000);

            Page page = context.newPage();
            page.navigate("https://example.com/downloads");

            // 1. Ожидание начала загрузки
            Download download = page.waitForDownload(() -> {
                page.click("#download-pdf");
            });

            // 2. Ожидание завершения
            System.out.println("Загрузка начата: " + download.url());
            System.out.println("Предполагаемое имя: " + download.suggestedFilename());

            // 3. Сохранение с правильным именем
            Path savePath = Paths.get("downloads/", download.suggestedFilename());
            download.saveAs(savePath);

            // 4. Проверка
            Assertions.assertTrue(Files.exists(savePath));
            System.out.println("Размер файла: " + Files.size(savePath) + " bytes");

            // 5. Очистка
            download.delete();
            browser.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ===================== Полный пример =====================

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch();
            Page page = browser.newPage();

            // Эмуляция обрыва сети
            page.route("**/*", route -> route.abort());

            Download download = page.waitForDownload(() -> {
                page.click("#download-button");
            });

            // Ожидание ошибки
            String error = download.failure();
            Assertions.assertNotNull(error);
            System.out.println("Ошибка загрузки: " + error);
        }
    }
}
