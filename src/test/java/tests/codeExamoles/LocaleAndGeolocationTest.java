package tests.codeExamoles;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import tests.BaseTest;

public class LocaleAndGeolocationTest extends BaseTest {

    @Test
    public void localeAndGeolocationTest() {

        // Настройка языка и геолокации
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setLocale("fr-FR")                                     // Устанавливаем французский язык
                .setGeolocation(48.8566, 2.3522)          // Устанавливаем геолокацию (Париж)
                .setPermissions(Collections.singletonList("geolocation")));  // Разрешаем доступ к геолокации

        Page page = browser.newPage();
        page.navigate("https://example.com");
        System.err.println("Язык браузера: " + page.evaluate("navigator.language"));

        /**
         * Зачем разрешать доступ к геолокации?
         * Браузеры по умолчанию запрашивают у пользователя разрешение на доступ к его местоположению (например, всплывающее окно «Сайт хочет узнать ваше местоположение»).
         * В автотестах такой интерактивности нет, поэтому:
         * 1. setPermissions("geolocation") предоставляет права браузеру использовать геолокацию без запроса подтверждения.
         * 2. Без этого разрешения сайт не получит координаты, даже если они заданы через setGeolocation(). Тест будет вести себя так, как если бы пользователь отказался делиться местоположением.
         * 
         * Это критично для проверки:
         * - Логики, зависящей от местоположения (например, показ локализованных цен или сервисов).
         * - Корректной работы карт, геофильтров, определения часового пояса.
         * - Сценариев, где доступ к геолокации — обязательное условие (например, доставка еды).
         */
    }
}
