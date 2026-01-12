package tests.codeexamples;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

import tests.BaseTest;

public class NavigationTest extends BaseTest {

    @Test
    public void navigationTest() {

        // ===================== 1. Основы навигации =====================  

        // Открытие страницы 
        page.navigate("https://example.com", new Page.NavigateOptions()
                .setTimeout(60000)                     // Макс. время ожидания (60 сек)
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED) // Ждать "domcontentloaded" вместо "load"
        );

        // Перезагрузка
        page.reload();   // Простая перезагрузка  
        page.reload(new Page.ReloadOptions().setWaitUntil(WaitUntilState.NETWORKIDLE)); // Ждать отсутствия сетевых запросов

        // Навигация по истории
        page.goBack();   // Назад (как кнопка браузера)  
        page.goForward(); // Вперед  


        // ===================== 2. Управление вкладками ===================== 

        // Создать новую вкладку в текущем контексте  
        BrowserContext context = browser.newContext();
        Page newTab = context.newPage();
        newTab.navigate("https://new-page.com");

        // Получить все вкладки контекста  
        List<Page> pages = context.pages();

        // Переключиться на вторую вкладку  
        Page secondTab = pages.get(1);
        secondTab.bringToFront(); // Активировать вкладку  

        // Закрыть вкладку  
        secondTab.close();


        // ===================== 3. Контроль размера окна =====================

        // Установить размер 1920x1080
        page.setViewportSize(1920, 1080);

        // Эмуляция мобильного устройства  
        page.setViewportSize(375, 812); // iPhone X

        // Активировать полноэкранный режим
        page.keyboard().press("F11");


        // ===================== 4. Работа с историей браузера ===================== 

        // Получить текущий URL  
        String currentUrl = page.url();

        // Получить историю навигации  
        List<String> history = (List<String>) page.evaluate("() => window.history.length");

        // Добавить запись в историю (JS)  
        page.evaluate("() => window.history.pushState({}, '', '/new-url')");


        // ===================== 5. Получение информации о странице ===================== 

        // page.title()	Заголовок страницы (<title>)

        // page.url()	Текущий URL

        // page.content()	HTML-содержимое страницы

        // page.isClosed()	Закрыта ли вкладка


        // ===================== 6. Лучшие практики ===================== 

        // Всегда указывайте WaitUntil:
        page.navigate("url", new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));

        // Закрывайте неиспользуемые вкладки:
        // for (Page tab : context.pages()) {  
        //     if (!tab.equals(mainPage)) tab.close();  
        // }

    }
}
