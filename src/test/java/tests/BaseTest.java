package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected Page page;

    @BeforeAll
    public static void setUp() {
        // Общая инициализация
        playwright = Playwright.create();

        // Headless-режим: По умолчанию Playwright запускает браузеры в headless-режиме (без графического интерфейса). 
        // Если вы хотите видеть действия в браузере, используйте setHeadless(false).
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(false) 
        );
    }

    @BeforeEach
    public void setupTest() {
        page = browser.newPage();
    }

    @AfterEach
    public void tearDownTest() {
        // if (page != null) page.close();
    }
 
    @AfterEach
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
} 
