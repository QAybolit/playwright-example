package tests.dialog;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class DialogExample {

    public static void main(String[] args) {

        // Dialog представляет диалоговые окна браузера (alert, confirm, prompt). Без обработчика тесты будут зависать!

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            page.onDialog(dialog -> {

                // Тип диалога (type)
                String type = dialog.type(); // "alert", "confirm", "prompt", "beforeunload"

                // Сообщение (message)
                String text = dialog.message(); // Текст в диалоге

                // Значение по умолчанию (defaultValue)
                // Для prompt
                String defaultValue = dialog.defaultValue();

                // Принятие (accept)
                dialog.accept(); // Подтвердить (OK)
                dialog.accept("Введённый текст"); // Для prompt

                // Отклонение (dismiss)
                dialog.dismiss(); // Отмена (Cancel)

                // Страница-источник (page)
                Page sourcePage = dialog.page(); // Страница, вызвавшая диалог
            });

            // ===================== Пример обработки =====================

            // Всегда вызывайте accept() или dismiss()!
            // Для beforeunload используйте только accept() (браузер игнорирует dismiss).

            page.onDialog(dialog -> {
                if ("alert".equals(dialog.type())) {
                    System.out.println("ALERT: " + dialog.message());
                    dialog.accept();
                } else if ("prompt".equals(dialog.type())) {
                    dialog.accept("Playwright");
                } else if ("confirm".equals(dialog.type())) {
                    dialog.dismiss();
                }
            });

            // Триггеры:
            page.evaluate("alert('Hello!')");
            page.evaluate("prompt('Enter name:')");
            page.evaluate("confirm('Delete?')");


            // ===================== Авторизация через HTTP Basic Auth: =====================

            page.onDialog(dialog -> {
                if (dialog.message().contains("Authentication required")) {
                    dialog.accept("admin:password");
                }
            });

            // ===================== Подтверждение действий: =====================

            page.onDialog(dialog -> dialog.accept()); // Все confirm -> OK
            page.click("button#delete"); // Вызовет confirm("Удалить?")

            // ===================== Обработка beforeunload: =====================

            page.onDialog(dialog -> dialog.accept()); // Подтвердить переход
            page.navigate("https://another-page.com"); // Триггерит beforeunload
        }
    }
}
