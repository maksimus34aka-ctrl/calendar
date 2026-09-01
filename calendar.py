# calendar.py
# Календарь с праздниками на Python

import sys
import datetime
import calendar as cal
from typing import Dict, List, Optional

# ANSI-цвета
RESET = "\033[0m"
RED = "\033[91m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
CYAN = "\033[96m"
BOLD = "\033[1m"

def colorize(text: str, color: str) -> str:
    return f"{color}{text}{RESET}"

# Праздники по странам (фиксированные даты: месяц, день -> название)
HOLIDAYS: Dict[str, Dict[int, Dict[int, str]]] = {
    'ru': {
        1: {1: "Новый год", 7: "Рождество Христово"},
        2: {23: "День защитника Отечества"},
        3: {8: "Международный женский день"},
        5: {1: "Праздник Весны и Труда", 9: "День Победы"},
        6: {12: "День России"},
        11: {4: "День народного единства"},
    },
    'us': {
        1: {1: "New Year's Day"},
        7: {4: "Independence Day"},
        11: {11: "Veterans Day"},
        12: {25: "Christmas Day"},
    },
    'by': {
        1: {1: "Новы год", 7: "Каляды"},
        3: {8: "Міжнародны жаночы дзень"},
        5: {1: "Дзень працы", 9: "Дзень Перамогі"},
        7: {3: "Дзень Незалежнасці"},
        11: {7: "Дзень Кастрычніцкай рэвалюцыі"},
    }
}

def get_holiday(month: int, day: int, country: str = 'ru') -> Optional[str]:
    return HOLIDAYS.get(country, {}).get(month, {}).get(day)

def print_calendar(year: int, month: int, country: str = 'ru') -> None:
    # Заголовок
    month_name = cal.month_name[month]
    print(f"\n{colorize(f'{month_name} {year}', BOLD + CYAN)}")
    print(colorize("Пн Вт Ср Чт Пт Сб Вс", BOLD))
    print("-" * 21)

    # Получаем календарь на месяц
    month_cal = cal.monthcalendar(year, month)
    for week in month_cal:
        week_str = []
        for day in week:
            if day == 0:
                week_str.append("  ")
            else:
                # Проверяем, является ли день выходным (суббота=5, воскресенье=6)
                # В monthcalendar: 0-пн, 1-вт, 2-ср, 3-чт, 4-пт, 5-сб, 6-вс
                # Нужно определить день недели для каждого day
                # Можно использовать weekday из datetime
                date_obj = datetime.date(year, month, day)
                weekday = date_obj.weekday()  # 0-пн, 6-вс
                is_weekend = weekday >= 5
                holiday = get_holiday(month, day, country)
                if holiday:
                    week_str.append(colorize(f"{day:2d}", RED))
                elif is_weekend:
                    week_str.append(colorize(f"{day:2d}", GREEN))
                else:
                    week_str.append(f"{day:2d}")
        print(" ".join(week_str))

    # Выводим список праздников
    holidays_this_month = HOLIDAYS.get(country, {}).get(month, {})
    if holidays_this_month:
        print("\n" + colorize("Праздники:", BOLD + YELLOW))
        for day, name in sorted(holidays_this_month.items()):
            print(f"  {day:2d} – {name}")
    print()

def interactive():
    print(colorize("Календарь с праздниками", BOLD + CYAN))
    try:
        year_input = input("Введите год (пусто для текущего): ").strip()
        if not year_input:
            year = datetime.datetime.now().year
        else:
            year = int(year_input)
        month_input = input("Введите месяц (1-12, пусто для текущего): ").strip()
        if not month_input:
            month = datetime.datetime.now().month
        else:
            month = int(month_input)
        if month < 1 or month > 12:
            print("Неверный месяц")
            return
        country = input("Введите код страны (ru/us/by, по умолчанию ru): ").strip() or 'ru'
        if country not in HOLIDAYS:
            print(f"Страна {country} не поддерживается, используем ru")
            country = 'ru'
        print_calendar(year, month, country)
    except ValueError:
        print("Ошибка ввода. Используйте числа.")

def main():
    if len(sys.argv) >= 3:
        try:
            year = int(sys.argv[1])
            month = int(sys.argv[2])
            country = sys.argv[3] if len(sys.argv) >= 4 else 'ru'
            if month < 1 or month > 12:
                print("Месяц должен быть от 1 до 12")
                return
            if country not in HOLIDAYS:
                print(f"Страна {country} не поддерживается, используем ru")
                country = 'ru'
            print_calendar(year, month, country)
        except ValueError:
            print("Неверный формат года или месяца")
    else:
        interactive()

if __name__ == '__main__':
    main()
