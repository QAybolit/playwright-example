package tests.video;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Video;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Paths;

public class VideoExample {

    /**
     * Объект Video предоставляет доступ к записи экрана страницы.
     * Видеозапись создаётся только при включении опции recordVideo при создании контекста браузера.
     * <br>
     * Методы:
     * - path()	Возвращает путь к видеофайлу	Только после закрытия контекста
     * - saveAs(path)	Сохраняет видео по указанному пути	Автоматически дожидается завершения записи
     * - delete()	Удаляет видеофайл	Дожидается завершения записи перед удалением
     */


    // ===================== Полный пример =====================
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static Video video; // Сохраняем ссылку на видео

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @BeforeEach
    void initContext(TestInfo testInfo) {
        // 1. Настройка записи видео
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));

        page = context.newPage();

        // 2. Сразу сохраняем ссылку на видео
        video = page.video();
    }

    @Test
    void testNavigation() {
        page.navigate("https://playwright.dev");
        page.getByText("Get Started").click();
        Assertions.assertTrue(page.getByText("enables reliable").isVisible());
    }

    @AfterEach
    void saveVideo(TestInfo testInfo) {
        // 3. Обязательно закрываем контекст ПЕРЕД сохранением видео
        context.close();

        if (video != null) {
            // 4. Теперь можно сохранять видео
            String testName = testInfo.getDisplayName()
                    .replace("(", "").replace(")", "")
                    .replace(" ", "_");

            try {
                video.saveAs(Paths.get("videos/" + testName + ".webm"));
                System.out.println("Video saved: videos/" + testName + ".webm");
            } catch (PlaywrightException e) {
                System.err.println("Error saving video: " + e.getMessage());
            }
        }
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }
}
