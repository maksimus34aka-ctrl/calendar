<?php
// calendar.php
// Календарь с праздниками на PHP

if (php_sapi_name() !== 'cli') {
    die("Это консольное приложение.\n");
}

// ANSI-цвета
define('RESET', "\033[0m");
define('RED', "\033[91m");
define('GREEN', "\033[92m");
define('YELLOW', "\033[93m");
define('CYAN', "\033[96m");
define('BOLD', "\033[1m");

function colorize($text, $color) {
    return $color . $text . RESET;
}

// Праздники
$holidays = [
    'ru' => [
        1 => [1 => 'Новый год', 7 => 'Рождество Христово'],
        2 => [23 => 'День защитника Отечества'],
        3 => [8 => 'Международный женский день'],
        5 => [1 => 'Праздник Весны и Труда', 9 => 'День Победы'],
        6 => [12 => 'День России'],
        11 => [4 => 'День народного единства'],
    ],
    'us' => [
        1 => [1 => "New Year's Day"],
        7 => [4 => 'Independence Day'],
        11 => [11 => 'Veterans Day'],
        12 => [25 => 'Christmas Day'],
    ],
    'by' => [
        1 => [1 => 'Новы год', 7 => 'Каляды'],
        3 => [8 => 'Міжнародны жаночы дзень'],
        5 => [1 => 'Дзень працы', 9 => 'Дзень Перамогі'],
        7 => [3 => 'Дзень Незалежнасці'],
        11 => [7 => 'Дзень Кастрычніцкай рэвалюцыі'],
    ],
];

function getHoliday($month, $day, $country) {
    global $holidays;
    return $holidays[$country][$month][$day] ?? null;
}

function printCalendar($year, $month, $country) {
    $monthNames = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
                   'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
    echo "\n" . colorize($monthNames[$month-1] . ' ' . $year, BOLD . CYAN) . "\n";
    echo colorize('Пн Вт Ср Чт Пт Сб Вс', BOLD) . "\n";
    echo "---------------------\n";

    $firstDay = (int)date('N', mktime(0,0,0,$month,1,$year)); // 1-пн, 7-вс
    $startOffset = $firstDay - 1;
    $daysInMonth = (int)date('t', mktime(0,0,0,$month,1,$year));

    $week = array_fill(0, 7, '  ');
    for ($i = 0; $i < $startOffset; $i++) {
        $week[$i] = '  ';
    }
    for ($d = 1; $d <= $daysInMonth; $d++) {
        $weekday = (int)date('N', mktime(0,0,0,$month,$d,$year)); // 1-пн, 7-вс
        $isWeekend = ($weekday == 6 || $weekday == 7);
        $holiday = getHoliday($month, $d, $country);
        if ($holiday) {
            $dayStr = colorize(sprintf("%2d", $d), RED);
        } elseif ($isWeekend) {
            $dayStr = colorize(sprintf("%2d", $d), GREEN);
        } else {
            $dayStr = sprintf("%2d", $d);
        }
        $week[$weekday-1] = $dayStr;
        if ($weekday == 7) {
            echo implode(' ', $week) . "\n";
            $week = array_fill(0, 7, '  ');
        }
    }
    // остаток
    if ($week[0] != '  ') {
        echo implode(' ', $week) . "\n";
    }

    // Праздники
    if (isset($holidays[$country][$month])) {
        echo "\n" . colorize('Праздники:', BOLD . YELLOW) . "\n";
        $days = array_keys($holidays[$country][$month]);
        sort($days);
        foreach ($days as $d) {
            echo sprintf("  %2d – %s\n", $d, $holidays[$country][$month][$d]);
        }
    }
    echo "\n";
}

function interactive() {
    echo colorize('Календарь с праздниками', BOLD . CYAN) . "\n";
    echo "Введите год (пусто для текущего): ";
    $yearInput = trim(fgets(STDIN));
    $year = $yearInput === '' ? (int)date('Y') : (int)$yearInput;
    echo "Введите месяц (1-12, пусто для текущего): ";
    $monthInput = trim(fgets(STDIN));
    $month = $monthInput === '' ? (int)date('n') : (int)$monthInput;
    if ($month < 1 || $month > 12) {
        echo "Неверный месяц\n";
        return;
    }
    echo "Введите код страны (ru/us/by, по умолчанию ru): ";
    $country = trim(fgets(STDIN));
    if ($country === '') $country = 'ru';
    if (!isset($holidays[$country])) {
        echo "Страна $country не поддерживается, используем ru\n";
        $country = 'ru';
    }
    printCalendar($year, $month, $country);
}

$args = array_slice($argv, 1);
if (count($args) >= 2) {
    $year = (int)$args[0];
    $month = (int)$args[1];
    $country = $args[2] ?? 'ru';
    if ($month < 1 || $month > 12) {
        echo "Месяц должен быть от 1 до 12\n";
        exit(1);
    }
    if (!isset($holidays[$country])) {
        echo "Страна $country не поддерживается, используем ru\n";
        $country = 'ru';
    }
    printCalendar($year, $month, $country);
} else {
    interactive();
}
