package tests.websocket;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.WebSocket;
import com.microsoft.playwright.WebSocketFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebSocketExample {


    /**
     * Класс WebSocket позволяет взаимодействовать с WebSocket-соединениями на странице:
     * - проверять передаваемые данные,
     * - отслеживать события и управлять подключениями.
     * - Для перехвата или модификации кадров используйте WebSocketRoute.
     * <br>
     * Основные методы
     * - isClosed() Проверяет, закрыто ли соединение. Возвращает: boolean
     * - url() Возвращает URL WebSocket-соединения. Возвращает: String
     * - waitForFrameReceived(Runnable callback, WaitForFrameReceivedOptions options) Ожидает получение кадра после выполнения действия.
     * Может использовать предикат для фильтрации кадров.
     * - waitForFrameSent(Runnable callback, WaitForFrameSentOptions options) Ожидает отправку кадра.
     * Аналогичен waitForFrameReceived, но для исходящих кадров.
     * <br>
     * События (Event Listeners):
     * - onClose(handler) Срабатывает при закрытии соединения.
     * - onFrameReceived(handler) Вызывается при получении кадра.
     * - onFrameSent(handler) Срабатывает при отправке кадра.
     * - onSocketError(handler) Обрабатывает ошибки соединения.
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

        // Ожидаем создание WebSocket при навигации
        webSocket = page.waitForWebSocket(() -> {
            page.navigate("https://websocket-demo.com");
        });
    }

    @Test
    void exampleText() {

        // Проверяет, закрыто ли соединение.
        boolean isClosed = webSocket.isClosed();

        //Возвращает URL WebSocket-соединения.
        String socketUrl = webSocket.url();

        // Ожидает получение кадра после выполнения действия. Может использовать предикат для фильтрации кадров.
        WebSocketFrame receivedFrame = webSocket.waitForFrameReceived(
                new WebSocket.WaitForFrameReceivedOptions()
                        .setPredicate(frame -> frame.text().contains("success"))
                        .setTimeout(5000)
                , () -> {
                    // Действие, инициирующее получение кадра
                });

        // Ожидает отправку кадра. Аналогичен waitForFrameReceived, но для исходящих кадров.
        WebSocketFrame sentFrame = webSocket.waitForFrameSent(
                new WebSocket.WaitForFrameSentOptions().setTimeout(3000),
                () -> {
                    // Действие, инициирующее отправку кадра
                });

        // Срабатывает при закрытии соединения.
        webSocket.onClose(webSocket -> System.out.println("Соединение закрыто"));

        // Вызывается при получении кадра.
        webSocket.onFrameReceived(frame ->
                System.out.println("Получен кадр: " + frame.text()));

        // Срабатывает при отправке кадра.
        webSocket.onFrameSent(frame ->
                System.out.println("Отправлен кадр: " + frame.text()));

        // Обрабатывает ошибки соединения.
        webSocket.onSocketError(error ->
                System.err.println("Ошибка: " + error));
    }

    @Test
    void testWebSocketCommunication() {
        // 1. Регистрируем обработчики событий
        webSocket.onClose(ws -> System.out.println("Connection closed"));
        webSocket.onFrameReceived(frame ->
                System.out.println("Received: " + frame.text()));
        webSocket.onFrameSent(frame ->
                System.out.println("Sent: " + frame.text()));

        // 2. Ожидаем получение кадра с помощью Runnable
        webSocket.waitForFrameReceived(new WebSocket.WaitForFrameReceivedOptions()
                .setPredicate(frame -> frame.text().contains("Hello"))
                .setTimeout(5000), new Runnable() {
            @Override
            public void run() {
                try {
                    // Действие, инициирующее ответ от сервера
                    page.click("#send-data-btn");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 3. Проверяем состояние соединения
        System.out.println("WebSocket URL: " + webSocket.url());
        System.out.println("Is closed: " + webSocket.isClosed());
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
