package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    public static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500));
    }

    @BeforeEach
    public void setUp() {
        page = browser.newPage();
    }

    @Test
    public void exampleTest() {
        page.navigate("https://playwright.dev");
        String title = page.title();

        assertTrue(title.contains("Playwright"));
    }

    @AfterEach
    public void tearDown() {
    }

    @AfterAll
    public static void closeBrowser() {
        browser.close();
        playwright.close();
    }
}
