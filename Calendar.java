// Calendar.java
// Календарь с праздниками на Java

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Calendar {
    // ANSI-цвета
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[91m";
    private static final String GREEN = "\u001B[92m";
    private static final String YELLOW = "\u001B[93m";
    private static final String CYAN = "\u001B[96m";
    private static final String BOLD = "\u001B[1m";

    private static String colorize(String text, String color) {
        return color + text + RESET;
    }

    // Праздники
    private static final Map<String, Map<Integer, Map<Integer, String>>> HOLIDAYS = new HashMap<>();
    static {
        Map<Integer, Map<Integer, String>> ru = new HashMap<>();
        ru.put(1, Map.of(1, "Новый год", 7, "Рождество Христово"));
        ru.put(2, Map.of(23, "День защитника Отечества"));
        ru.put(3, Map.of(8, "Международный женский день"));
        ru.put(5, Map.of(1, "Праздник Весны и Труда", 9, "День Победы"));
        ru.put(6, Map.of(12, "День России"));
        ru.put(11, Map.of(4, "День народного единства"));
        HOLIDAYS.put("ru", ru);

        Map<Integer, Map<Integer, String>> us = new HashMap<>();
        us.put(1, Map.of(1, "New Year's Day"));
        us.put(7, Map.of(4, "Independence Day"));
        us.put(11, Map.of(11, "Veterans Day"));
        us.put(12, Map.of(25, "Christmas Day"));
        HOLIDAYS.put("us", us);

        Map<Integer, Map<Integer, String>> by = new HashMap<>();
        by.put(1, Map.of(1, "Новы год", 7, "Каляды"));
        by.put(3, Map.of(8, "Міжнародны жаночы дзень"));
        by.put(5, Map.of(1, "Дзень працы", 9, "Дзень Перамогі"));
        by.put(7, Map.of(3, "Дзень Незалежнасці"));
        by.put(11, Map.of(7, "Дзень Кастрычніцкай рэвалюцыі"));
        HOLIDAYS.put("by", by);
    }

    private static String getHoliday(int month, int day, String country) {
        Map<Integer, Map<Integer, String>> c = HOLIDAYS.get(country);
        if (c == null) return null;
        Map<Integer, String> m = c.get(month);
        if (m == null) return null;
        return m.get(day);
    }

    private static void printCalendar(int year, int month, String country) {
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                               "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        System.out.printf("\n%s\n", colorize(monthNames[month-1] + " " + year, BOLD + CYAN));
        System.out.println(colorize("Пн Вт Ср Чт Пт Сб Вс", BOLD));
        System.out.println("---------------------");

        LocalDate first = LocalDate.of(year, month, 1);
        int startOffset = first.getDayOfWeek().getValue() - 1; // 1-пн, 7-вс -> 0-6
        int daysInMonth = first.lengthOfMonth();

        StringBuilder[] week = new StringBuilder[7];
        for (int i = 0; i < startOffset; i++) {
            week[i] = new StringBuilder("  ");
        }
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = LocalDate.of(year, month, d);
            int weekday = date.getDayOfWeek().getValue(); // 1-пн, 7-вс
            boolean isWeekend = (weekday == 6 || weekday == 7);
            String holiday = getHoliday(month, d, country);
            String dayStr;
            if (holiday != null) {
                dayStr = colorize(String.format("%2d", d), RED);
            } else if (isWeekend) {
                dayStr = colorize(String.format("%2d", d), GREEN);
            } else {
                dayStr = String.format("%2d", d);
            }
            week[weekday-1] = new StringBuilder(dayStr);
            if (weekday == 7) {
                for (int i = 0; i < 7; i++) {
                    System.out.print(week[i].toString() + (i < 6 ? " " : ""));
                }
                System.out.println();
                week = new StringBuilder[7];
                for (int i = 0; i < 7; i++) week[i] = new StringBuilder();
            }
        }
        // остаток недели
        boolean printed = false;
        for (int i = 0; i < 7; i++) {
            if (week[i] != null && week[i].length() > 0) {
                if (!printed) { printed = true; }
                System.out.print(week[i].toString() + (i < 6 ? " " : ""));
            } else {
                System.out.print("  " + (i < 6 ? " " : ""));
            }
        }
        if (printed) System.out.println();

        // Праздники
        Map<Integer, String> holidaysThisMonth = HOLIDAYS.get(country).get(month);
        if (holidaysThisMonth != null && !holidaysThisMonth.isEmpty()) {
            System.out.printf("\n%s\n", colorize("Праздники:", BOLD + YELLOW));
            List<Integer> sortedDays = new ArrayList<>(holidaysThisMonth.keySet());
            Collections.sort(sortedDays);
            for (int day : sortedDays) {
                System.out.printf("  %2d – %s\n", day, holidaysThisMonth.get(day));
            }
        }
        System.out.println();
    }

    private static void interactive() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(colorize("Календарь с праздниками", BOLD + CYAN));
        System.out.print("Введите год (пусто для текущего): ");
        String yearInput = reader.readLine().trim();
        int year = yearInput.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(yearInput);
        System.out.print("Введите месяц (1-12, пусто для текущего): ");
        String monthInput = reader.readLine().trim();
        int month = monthInput.isEmpty() ? LocalDate.now().getMonthValue() : Integer.parseInt(monthInput);
        if (month < 1 || month > 12) {
            System.out.println("Неверный месяц");
            return;
        }
        System.out.print("Введите код страны (ru/us/by, по умолчанию ru): ");
        String country = reader.readLine().trim();
        if (country.isEmpty()) country = "ru";
        if (!HOLIDAYS.containsKey(country)) {
            System.out.printf("Страна %s не поддерживается, используем ru\n", country);
            country = "ru";
        }
        printCalendar(year, month, country);
    }

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            int year = Integer.parseInt(args[0]);
            int month = Integer.parseInt(args[1]);
            String country = args.length >= 3 ? args[2] : "ru";
            if (month < 1 || month > 12) {
                System.out.println("Месяц должен быть от 1 до 12");
                return;
            }
            if (!HOLIDAYS.containsKey(country)) {
                System.out.printf("Страна %s не поддерживается, используем ru\n", country);
                country = "ru";
            }
            printCalendar(year, month, country);
        } else {
            interactive();
        }
    }
}
