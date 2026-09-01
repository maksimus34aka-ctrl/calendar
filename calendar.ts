// calendar.ts
// Календарь с праздниками на TypeScript

import * as readline from 'readline';

// ANSI-цвета
const RESET = '\x1b[0m';
const RED = '\x1b[91m';
const GREEN = '\x1b[92m';
const YELLOW = '\x1b[93m';
const CYAN = '\x1b[96m';
const BOLD = '\x1b[1m';

function colorize(text: string, color: string): string {
    return `${color}${text}${RESET}`;
}

type HolidaysMap = { [month: number]: { [day: number]: string } };

const HOLIDAYS: { [country: string]: HolidaysMap } = {
    ru: {
        1: {1: 'Новый год', 7: 'Рождество Христово'},
        2: {23: 'День защитника Отечества'},
        3: {8: 'Международный женский день'},
        5: {1: 'Праздник Весны и Труда', 9: 'День Победы'},
        6: {12: 'День России'},
        11: {4: 'День народного единства'},
    },
    us: {
        1: {1: "New Year's Day"},
        7: {4: 'Independence Day'},
        11: {11: 'Veterans Day'},
        12: {25: 'Christmas Day'},
    },
    by: {
        1: {1: 'Новы год', 7: 'Каляды'},
        3: {8: 'Міжнародны жаночы дзень'},
        5: {1: 'Дзень працы', 9: 'Дзень Перамогі'},
        7: {3: 'Дзень Незалежнасці'},
        11: {7: 'Дзень Кастрычніцкай рэвалюцыі'},
    }
};

function getHoliday(month: number, day: number, country: string = 'ru'): string | null {
    return HOLIDAYS[country]?.[month]?.[day] || null;
}

function printCalendar(year: number, month: number, country: string = 'ru'): void {
    const monthNames = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
                        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
    console.log(`\n${colorize(`${monthNames[month-1]} ${year}`, BOLD + CYAN)}`);
    console.log(colorize('Пн Вт Ср Чт Пт Сб Вс', BOLD));
    console.log('-'.repeat(21));

    const firstDay = new Date(year, month-1, 1).getDay();
    let startOffset = (firstDay === 0) ? 6 : firstDay - 1;
    const daysInMonth = new Date(year, month, 0).getDate();

    let week: string[] = [];
    for (let i = 0; i < startOffset; i++) week.push('  ');
    for (let d = 1; d <= daysInMonth; d++) {
        const dateObj = new Date(year, month-1, d);
        const weekday = dateObj.getDay();
        const isWeekend = (weekday === 0 || weekday === 6);
        const holiday = getHoliday(month, d, country);
        let dayStr: string;
        if (holiday) {
            dayStr = colorize(`${d}`.padStart(2, ' '), RED);
        } else if (isWeekend) {
            dayStr = colorize(`${d}`.padStart(2, ' '), GREEN);
        } else {
            dayStr = `${d}`.padStart(2, ' ');
        }
        week.push(dayStr);
        if (week.length === 7) {
            console.log(week.join(' '));
            week = [];
        }
    }
    if (week.length > 0) {
        console.log(week.join(' '));
    }

    const holidaysThisMonth = HOLIDAYS[country]?.[month];
    if (holidaysThisMonth && Object.keys(holidaysThisMonth).length > 0) {
        console.log(`\n${colorize('Праздники:', BOLD + YELLOW)}`);
        const sorted = Object.keys(holidaysThisMonth).sort((a,b) => Number(a) - Number(b));
        for (const day of sorted) {
            console.log(`  ${day.padStart(2, ' ')} – ${holidaysThisMonth[Number(day)]}`);
        }
    }
    console.log();
}

function interactive(): void {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });
    console.log(colorize('Календарь с праздниками', BOLD + CYAN));
    rl.question('Введите год (пусто для текущего): ', (yearInput) => {
        const year = yearInput.trim() ? parseInt(yearInput) : new Date().getFullYear();
        rl.question('Введите месяц (1-12, пусто для текущего): ', (monthInput) => {
            let month = monthInput.trim() ? parseInt(monthInput) : new Date().getMonth() + 1;
            if (month < 1 || month > 12) {
                console.log('Неверный месяц');
                rl.close();
                return;
            }
            rl.question('Введите код страны (ru/us/by, по умолчанию ru): ', (country) => {
                country = country.trim() || 'ru';
                if (!HOLIDAYS[country]) {
                    console.log(`Страна ${country} не поддерживается, используем ru`);
                    country = 'ru';
                }
                printCalendar(year, month, country);
                rl.close();
            });
        });
    });
}

function main(): void {
    const args = process.argv.slice(2);
    if (args.length >= 2) {
        const year = parseInt(args[0]);
        const month = parseInt(args[1]);
        const country = args[2] || 'ru';
        if (isNaN(year) || isNaN(month) || month < 1 || month > 12) {
            console.log('Неверный формат года или месяца');
            process.exit(1);
        }
        if (!HOLIDAYS[country]) {
            console.log(`Страна ${country} не поддерживается, используем ru`);
            printCalendar(year, month, 'ru');
        } else {
            printCalendar(year, month, country);
        }
    } else {
        interactive();
    }
}

main();
