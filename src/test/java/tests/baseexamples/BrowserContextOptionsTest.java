package tests.baseexamples;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ColorScheme;
import com.microsoft.playwright.options.Media;

import tests.BaseTest;

public class BrowserContextOptionsTest extends BaseTest {

    @Test
    public void deviceEmulationTest() {

        // Создаем контекст с параметрами устройства iPhone 11
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                        .setViewportSize(414, 896)      // Ширина x Высота
                        .setDeviceScaleFactor(2)   // Плотность пикселей
                        .setIsMobile(true)                  // Мобильный режим
                        .setHasTouch(true)                  // Поддержка touch-событий
        );

        Page page = context.newPage();
        page.navigate("https://example.com");

        System.out.println("User Agent: " + page.evaluate("navigator.userAgent"));
        System.out.println("Viewport: " + page.evaluate("window.innerWidth + 'x' + window.innerHeight"));

        context.close();

        /**
         * 1. setUserAgent() - строка идентификации браузера/устройства
         *
         * 2. setViewportSize(width, height) - размеры экрана в пикселях
         *
         * 3. setDeviceScaleFactor() - плотность пикселей (2 для Retina, 3 для Super Retina)
         *
         * 4. setIsMobile(true) - включение мобильного режима
         *
         * 5. setHasTouch(true) - эмуляция touch-событий
         */

        // Создать контекст с заданным viewport
        BrowserContext newContext = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 1024));

        // Изменить размер viewport для отдельной страницы
        page.setViewportSize(1600, 1200);

        // Эмулировать высокое разрешение DPI
        BrowserContext anotherContext = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(2560, 1440)
                .setDeviceScaleFactor(2));

        // isMobile - Отвечает за то, учитывается ли мета-тег viewport и включены ли события касания
        BrowserContext notMobileContext = browser.newContext(new Browser.NewContextOptions().setIsMobile(false));

        // Эмулируйте локализацию и часовой пояс пользователя, которые могут быть установлены глобально для всех тестов 
        // в конфигурации и затем переопределены для конкретных тестов
        BrowserContext localContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setLocale("de-DE")
                        .setTimezoneId("Europe/Berlin")
        );

        // Разрешите приложению показывать системные уведомления
        BrowserContext logContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setPermissions(Arrays.asList("notifications"))
        );

        // Предоставьте разрешения "geolocation" и настройте геолокацию для конкретной области
        BrowserContext geoContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setGeolocation(41.890221, 12.492348)
                        .setPermissions(Arrays.asList("geolocation"))
        );

        // Эмулируйте "colorScheme" пользователя. Поддерживаемые значения: 'светлый' и 'тёмный'.

        // Создать контекст с темным режимом
        BrowserContext darkContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setColorScheme(ColorScheme.DARK)  // или "light"
        );

        // Создать страницу с темным режимом
        Page darkPage = browser.newPage(
                new Browser.NewPageOptions().setColorScheme(ColorScheme.DARK)   // или "light"
        );

        // Изменить цветовую схему для страницы
        darkPage.emulateMedia(new Page.EmulateMediaOptions().setColorScheme(ColorScheme.LIGHT));

        // Изменить медиа для страницы
        darkPage.emulateMedia(new Page.EmulateMediaOptions().setMedia(Media.PRINT));

        // User Agent встроен в устройство, поэтому вам редко придётся его менять. 
        // Если вам нужно протестировать другой user agent, вы можете переопределить его с помощью свойства userAgent
        BrowserContext userAgentContext = browser.newContext(
                new Browser.NewContextOptions().setUserAgent("My User Agent")
        );

        // Вы можете имитировать автономный режим
        BrowserContext offlineContext = browser.newContext(
                new Browser.NewContextOptions().setOffline(true)
        );

        // Имитируйте при необходимости сценарии пользователя, у которого отключен JavaScript
        BrowserContext withoutJsContext = browser.newContext(
                new Browser.NewContextOptions().setJavaScriptEnabled(false)
        );

    }

}
