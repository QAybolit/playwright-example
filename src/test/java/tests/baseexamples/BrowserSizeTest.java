package tests.baseexamples;

import org.junit.jupiter.api.Test;

import tests.BaseTest;

public class BrowserSizeTest extends BaseTest {

    @Test
    public void testBrowserSize() {
        // Устанавливаем размер окна браузера
        // Ширина: 1280px, Высота: 720px
        page.setViewportSize(1280, 720);

        page.navigate("https://example.com");

        // Возвращает текущий размер окна
        System.err.println("Размер окна: " + page.viewportSize().width + " x " + page.viewportSize().height);
    }

}
