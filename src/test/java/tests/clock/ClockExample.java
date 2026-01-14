package tests.clock;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Clock;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ClockExample {

    public static void main(String[] args) {

        /**
         * Clock позволяет точно эмулировать и контролировать время в браузере.
         * Это критично для тестирования:
         * - Таймеров, анимаций, кэширования
         * - Поведения, зависящего от дат (например, акции со сроком действия)
         * - Расписаний и отложенных операций
         * - Особенность: Часы устанавливаются для всего BrowserContext (все страницы/фреймы синхронизированы).
         */

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            // ===================== Инициализация часов (install) =====================

            // Установка с текущим временем
            page.clock().install();

            // Установка конкретной даты
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            page.clock().install(new Clock.InstallOptions()
                    .setTime(format.parse("2025-01-01"))
            );

            // ===================== Фиксация времени (setFixedTime) =====================

            // Фиксированное время для всех операций
            // Таймеры работают в "замороженном" режиме
            page.clock().setFixedTime("2030-12-31T23:59:59");

            // ===================== Перемотка времени (fastForward) =====================

            // Таймеры срабатывают только один раз за период.

            // Перемотка на 1 час
            page.clock().fastForward("01:00:00");

            // Перемотка на 30 минут (строка)
            page.clock().fastForward("30:00");

            // Перемотка на 5000 мс (число)
            page.clock().fastForward(5000);

            // ===================== Пошаговое выполнение (runFor) =====================

            // Запуск всех таймеров в интервале
            // Имитирует реальное выполнение всех колбэков в указанном интервале
            page.clock().runFor("00:10:00"); // 10 минут

            // ===================== Приостановка (pauseAt) =====================

            // Установка времени и пауза
            page.clock().pauseAt("2024-06-15T12:00:00");

            // Возобновление времени
            page.clock().resume();

            // Сценарий:
            // 1. Начальная настройка (до навигации)
            page.clock().install(new Clock.InstallOptions()
                    .setTime(format.parse("2024-06-15T10:00:00"))
            );

            // 2. Загрузка страницы
            page.navigate("https://event-page.com");

            // 3. Переход к началу события и пауза
            page.clock().pauseAt("2024-06-15T12:00:00");

            // ===================== Смена системного времени (setSystemTime) =====================

            // Эмуляция смены часового пояса
            page.clock().setSystemTime("2024-03-31T02:00:00+03:00");

            // Используется для тестирования:
            // Реакции на переход летнее/зимнее время
            // Логики, зависящей от часовых поясов.


            // ===================== Советы по использованию =====================

            // Инициализация до навигации:
            // Правильно:
//            page.clock().install(options);
//            page.navigate(url);

            // Ошибка:
//            page.navigate(url);
//            page.clock().install(options); // Таймеры при загрузке уже сработали!

            // Форматы времени:
            // Число: миллисекунды (3000)
            // Строка: "15" (секунды), "01:30" (минуты), "02:30:45" (часы)
            // Date: Точная дата

            // Отладка:
            // Текущее "поддельное" время
            Object date = page.evaluate("() => new Date().toString()");
            System.out.println(date);


        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
