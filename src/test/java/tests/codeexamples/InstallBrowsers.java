package tests.codeexamples;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

public class InstallBrowsers {

    private static Playwright playwright;       // Объект Playwright
    private static Browser chromeBrowser;       // Chromium браузер
    private static Browser firefoxBrowser;      // Firefox браузер
    private static Browser webkitBrowser;       // WebKit браузер

    @BeforeAll
    public static void setUp() {
        playwright = Playwright.create();       // Инициализация Playwright

        chromeBrowser = playwright.chromium().launch();     // Запуск Chromium
        firefoxBrowser = playwright.firefox().launch();     // Запуск Firefox
        webkitBrowser = playwright.webkit().launch();       // Запуск WebKit
    }

    @AfterAll
    public static void tearDown() {
        if (chromeBrowser != null) chromeBrowser.close();       // Закрыть Chromium
        if (firefoxBrowser != null) firefoxBrowser.close();         // Закрыть Firefox
        if (webkitBrowser != null) webkitBrowser.close();           // Закрыть WebKit
        if (playwright != null) playwright.close();                 // Закрыть Playwright
    }

    @Test
    public void dummyTest() {
        System.out.println("Browsers запущены и готовы к тестам."); // Заглушка теста
    }
}
