package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ViewportSize;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrowserManagementTest extends BaseTest {

    @Test
    @DisplayName("Управление браузером: контексты, страницы, эмуляции")
    public void browserManagementTest() {
        // 1. РАБОТКА С КОНТЕКСТАМИ
        BrowserContext context1 = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1200, 720)
                .setLocale("ru-RU")
                .setPermissions(List.of("geolocation")));

        Page page1 = context1.newPage();

        // Изменение стратегии ожидания загрузки
        page1.navigate("https://demoqa.com/login",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        // Явное ожидание появления формы
        page1.waitForSelector("#userForm",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(15000));

        // Авторизация
        page1.fill("#userName", "testuser");
        page1.fill("#password", "Test123!");

        // Ожидание кликабельной кнопки
        page1.waitForSelector("#login",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.ATTACHED));
        page1.click("#login");

        // Ожидание завершения авторизации
        page1.waitForSelector("#userName-value",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000)
        );
        assertTrue(page1.textContent("#userName-value").contains("testuser"),
                "Пользователь должен быть авторизован");

        // 2. РАБОТА СО СТРАНИЦЕЙ
        Page page2 = context1.newPage();
        page2.navigate("https://demoqa.com/profile",
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
        );

        page2.waitForSelector("#userName-value", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(page2.isVisible("#userName-value"));

        // 3. ИЗОЛИРОВАННЫЕ КОНТЕКСТЫ
        BrowserContext context2 = browser.newContext();
        Page page3 = context2.newPage();
        page3.navigate("https://demoqa.com/login",
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        // Ждем появления формы логина
        page3.waitForSelector("#userForm",
                new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(page3.isVisible("#userForm"), "Форма логина должна быть видна");

        // 4. ЭМУЛЯЦИЯ УСТРОЙСТВ
        // Эмуляция iphone 12 pro - параметры заданы вручную
        Browser.NewContextOptions iPhone12ProOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like MacOS X) " +
                        "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1")
                .setViewportSize(390, 844)
                .setDeviceScaleFactor(3)
                .setIsMobile(true)
                .setHasTouch(true);

        BrowserContext mobileContext = browser.newContext(iPhone12ProOptions);
        Page mobilePage = mobileContext.newPage();
        mobilePage.navigate("https://demoqa.com",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        ViewportSize viewportSize = mobilePage.viewportSize();

        assertEquals(390, viewportSize.width, "Ширирна viewport должна соответсвовать iPhone");
        assertEquals(844, viewportSize.height, "Высота viewport должна соответсвовать iPhone");

        mobilePage.tap("text=Elements");
        mobilePage.waitForSelector(".element-list",
                new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(mobilePage.isVisible(".element-list"), "Мобильное меню должно отображаться");

        // 5. СОХРАНЕНИЕ СОСТОЯНИЯ АВТОРИЗАЦИИ
        context1.storageState(new BrowserContext.StorageStateOptions()
                .setPath(Paths.get("auth-state.json")));

        BrowserContext restoredContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setStorageStatePath(Paths.get("auth-state.json"))
        );

        // Закрываем контексты
        context1.close();
        context2.close();
        mobileContext.close();
        restoredContext.close();
    }
}
