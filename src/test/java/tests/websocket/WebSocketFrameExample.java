package tests.websocket;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.WebSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebSocketFrameExample {

    /**
     * WebSocketFrame представляет отдельные фреймы данных, передаваемые через WebSocket-соединение.
     * Каждый фрейм содержит полезную нагрузку, доступную в текстовом или бинарном формате.
     * <br>
     * Основные методы:
     * - binary()
     * Возвращает бинарные данные фрейма в виде массива байтов.
     * Возвращает: byte[] или null (если фрейм текстовый)
     * - text()
     * Возвращает текстовое содержимое фрейма.
     * Возвращает: String или null (если фрейм бинарный)
     */

    // ===================== Полный пример тестового класса =====================
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    WebSocket webSocket;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        // Ожидаем создание WebSocket
        webSocket = page.waitForWebSocket(() -> {
            page.navigate("https://websocket-demo.com");
        });
    }

    @Test
    void testWebSocketFrames() {
        // Регистрируем обработчик входящих фреймов
        webSocket.onFrameReceived(frame -> {
            // Проверяем тип фрейма
            if (frame.text() != null) {
                System.out.println("Текстовый фрейм: " + frame.text());
                Assertions.assertTrue(frame.text().contains("Данные"));
            } else if (frame.binary() != null) {
                System.out.println("Бинарный фрейм. Размер: " + frame.binary().length + " байт");
            }
        });

        // Инициируем получение фреймов
        webSocket.waitForFrameReceived(
                new WebSocket.WaitForFrameReceivedOptions()
                        .setTimeout(8000),
                () -> page.click("#trigger-frames-btn")
        );

        // Анализируем отправленные фреймы
        webSocket.onFrameSent(sentFrame -> {
            if (sentFrame.text() != null) {
                System.out.println("Отправлен текст: " + sentFrame.text());
            }
        });
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
