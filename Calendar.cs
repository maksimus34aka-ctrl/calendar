// Calendar.cs
// Календарь с праздниками на C#

using System;
using System.Collections.Generic;
using System.Linq;

class Calendar
{
    private const string RESET = "\u001B[0m";
    private const string RED = "\u001B[91m";
    private const string GREEN = "\u001B[92m";
    private const string YELLOW = "\u001B[93m";
    private const string CYAN = "\u001B[96m";
    private const string BOLD = "\u001B[1m";

    private static string Colorize(string text, string color) => color + text + RESET;

    private static readonly Dictionary<string, Dictionary<int, Dictionary<int, string>>> Holidays = new()
    {
        ["ru"] = new()
        {
            [1] = new() { {1, "Новый год"}, {7, "Рождество Христово"} },
            [2] = new() { {23, "День защитника Отечества"} },
            [3] = new() { {8, "Международный женский день"} },
            [5] = new() { {1, "Праздник Весны и Труда"}, {9, "День Победы"} },
            [6] = new() { {12, "День России"} },
            [11] = new() { {4, "День народного единства"} },
        },
        ["us"] = new()
        {
            [1] = new() { {1, "New Year's Day"} },
            [7] = new() { {4, "Independence Day"} },
            [11] = new() { {11, "Veterans Day"} },
            [12] = new() { {25, "Christmas Day"} },
        },
        ["by"] = new()
        {
            [1] = new() { {1, "Новы год"}, {7, "Каляды"} },
            [3] = new() { {8, "Міжнародны жаночы дзень"} },
            [5] = new() { {1, "Дзень працы"}, {9, "Дзень Перамогі"} },
            [7] = new() { {3, "Дзень Незалежнасці"} },
            [11] = new() { {7, "Дзень Кастрычніцкай рэвалюцыі"} },
        }
    };

    private static string GetHoliday(int month, int day, string country)
    {
        if (Holidays.TryGetValue(country, out var c) &&
            c.TryGetValue(month, out var m) &&
            m.TryGetValue(day, out var name))
            return name;
        return null;
    }

    private static void PrintCalendar(int year, int month, string country)
    {
        string[] monthNames = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                                "Июль", "Август", "Сентябрь", "Окторябрь", "Ноябрь", "Декабрь" };
        Console.WriteLine($"\n{Colorize($"{monthNames[month-1]} {year}", BOLD + CYAN)}");
        Console.WriteLine(Colorize("Пн Вт Ср Чт Пт Сб Вс", BOLD));
        Console.WriteLine("---------------------");

        DateTime first = new DateTime(year, month, 1);
        int startOffset = ((int)first.DayOfWeek + 6) % 7; // 0-пн, 6-вс
        int daysInMonth = DateTime.DaysInMonth(year, month);

        string[] week = new string[7];
        for (int i = 0; i < startOffset; i++) week[i] = "  ";
        for (int d = 1; d <= daysInMonth; d++)
        {
            DateTime date = new DateTime(year, month, d);
            int weekday = ((int)date.DayOfWeek + 6) % 7; // 0-пн, 6-вс
            bool isWeekend = (weekday == 5 || weekday == 6);
            string holiday = GetHoliday(month, d, country);
            string dayStr;
            if (holiday != null)
                dayStr = Colorize($"{d,2}", RED);
            else if (isWeekend)
                dayStr = Colorize($"{d,2}", GREEN);
            else
                dayStr = $"{d,2}";
            week[weekday] = dayStr;
            if (weekday == 6)
            {
                Console.WriteLine(string.Join(" ", week));
                week = new string[7];
            }
        }
        // остаток
        if (week[0] != null)
        {
            Console.WriteLine(string.Join(" ", week.Select(s => s ?? "  ")));
        }

        // Праздники
        if (Holidays.TryGetValue(country, out var c2) && c2.TryGetValue(month, out var m2))
        {
            Console.WriteLine($"\n{Colorize("Праздники:", BOLD + YELLOW)}");
            foreach (var day in m2.Keys.OrderBy(k => k))
                Console.WriteLine($"  {day,2} – {m2[day]}");
        }
        Console.WriteLine();
    }

    private static void Interactive()
    {
        Console.WriteLine(Colorize("Календарь с праздниками", BOLD + CYAN));
        Console.Write("Введите год (пусто для текущего): ");
        string yearInput = Console.ReadLine().Trim();
        int year = string.IsNullOrEmpty(yearInput) ? DateTime.Now.Year : int.Parse(yearInput);
        Console.Write("Введите месяц (1-12, пусто для текущего): ");
        string monthInput = Console.ReadLine().Trim();
        int month = string.IsNullOrEmpty(monthInput) ? DateTime.Now.Month : int.Parse(monthInput);
        if (month < 1 || month > 12)
        {
            Console.WriteLine("Неверный месяц");
            return;
        }
        Console.Write("Введите код страны (ru/us/by, по умолчанию ru): ");
        string country = Console.ReadLine().Trim();
        if (string.IsNullOrEmpty(country)) country = "ru";
        if (!Holidays.ContainsKey(country))
        {
            Console.WriteLine($"Страна {country} не поддерживается, используем ru");
            country = "ru";
        }
        PrintCalendar(year, month, country);
    }

    static void Main(string[] args)
    {
        if (args.Length >= 2)
        {
            int year = int.Parse(args[0]);
            int month = int.Parse(args[1]);
            string country = args.Length >= 3 ? args[2] : "ru";
            if (month < 1 || month > 12)
            {
                Console.WriteLine("Месяц должен быть от 1 до 12");
                return;
            }
            if (!Holidays.ContainsKey(country))
            {
                Console.WriteLine($"Страна {country} не поддерживается, используем ru");
                country = "ru";
            }
            PrintCalendar(year, month, country);
        }
        else
        {
            Interactive();
        }
    }
}
