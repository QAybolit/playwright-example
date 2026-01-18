package tests.baseexamples;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Proxy;

import tests.BaseTest;

public class ProxyTest extends BaseTest {

    @Test
    public void proxyTest() {
        // Настройка прокси
        // setProxy(new Proxy("http://my-proxy-server:3128")): Указывает адрес прокси-сервера.
        Browser proxyBrowser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setProxy(new Proxy("http://my-proxy-server:3128"))
        );

        Page page = proxyBrowser.newPage();
        page.navigate("https://example.com");

        System.err.println("Заголовок страницы: " + page.title());

        /**
         * Прокси-сервер — это промежуточный сервер между вашим приложением и интернетом:
         * 1. Маскирует ваш реальный IP-адрес (запросы идут через IP прокси)
         * 2. Фильтрует или логирует трафик (например, для анализа ошибок)
         * 3. Обходит географические ограничения (имитирует доступ из другой страны)
         * <br>
         * Зачем настраивать прокси в Playwright?
         * 1. Тестирование геолокации. Проверка, как приложение работает для пользователей из разных регионов (например, контент для ЕС в сравнении с РФ).
         * 2. Обход блокировок. Некоторые сайты блокируют запросы от автоматизированных скриптов. Прокси помогает избежать этой блокировки.
         * 3. Безопасность и изоляция. Тесты выполняются через отдельный сетевой канал, что защищает основную инфраструктуру.
         * 4. Отладка сетевых проблем. Логирование трафика через прокси помогает анализировать ошибки (например, некорректные заголовки).
         * <br>
         * Что происходит в коде?
         * 1. Playwright запускает браузер Chromium, указывая ему использовать прокси-сервер http://my-proxy-server:3128.
         * 2. Весь трафик браузера (включая переход по page.navigate()) будет проходить через этот прокси.
         * 3. Важно: Playwright настраивает прокси только для экземпляра браузера в тесте — системные настройки не меняются.
         * 4. Если прокси требует аутентификации, укажите логин / пароль: .setProxy(new Proxy("http://login:password@my-proxy-server:3128"))
         * 5. Поддерживаются разные типы прокси: HTTP, HTTPS.
         * 6. Для сложных сценариев можно настроить исключения (например, не использовать прокси для локальных URL)
         */


    }

}
