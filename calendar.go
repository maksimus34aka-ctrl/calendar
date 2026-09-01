// calendar.go
// Календарь с праздниками на Go

package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
	"time"
)

// ANSI-цвета
const (
	reset  = "\033[0m"
	red    = "\033[91m"
	green  = "\033[92m"
	yellow = "\033[93m"
	cyan   = "\033[96m"
	bold   = "\033[1m"
)

func colorize(text, color string) string {
	return color + text + reset
}

// Праздники
var holidays = map[string]map[int]map[int]string{
	"ru": {
		1: {1: "Новый год", 7: "Рождество Христово"},
		2: {23: "День защитника Отечества"},
		3: {8: "Международный женский день"},
		5: {1: "Праздник Весны и Труда", 9: "День Победы"},
		6: {12: "День России"},
		11: {4: "День народного единства"},
	},
	"us": {
		1:  {1: "New Year's Day"},
		7:  {4: "Independence Day"},
		11: {11: "Veterans Day"},
		12: {25: "Christmas Day"},
	},
	"by": {
		1: {1: "Новы год", 7: "Каляды"},
		3: {8: "Міжнародны жаночы дзень"},
		5: {1: "Дзень працы", 9: "Дзень Перамогі"},
		7: {3: "Дзень Незалежнасці"},
		11: {7: "Дзень Кастрычніцкай рэвалюцыі"},
	},
}

func getHoliday(month, day int, country string) string {
	if m, ok := holidays[country][month]; ok {
		if name, ok := m[day]; ok {
			return name
		}
	}
	return ""
}

func printCalendar(year, month int, country string) {
	monthNames := []string{"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
		"Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"}
	fmt.Printf("\n%s\n", colorize(fmt.Sprintf("%s %d", monthNames[month-1], year), bold+cyan))
	fmt.Println(colorize("Пн Вт Ср Чт Пт Сб Вс", bold))
	fmt.Println("---------------------")

	firstDay := time.Date(year, time.Month(month), 1, 0, 0, 0, 0, time.UTC).Weekday()
	// Приводим к понедельнику как 0
	startOffset := int(firstDay) - 1
	if startOffset < 0 {
		startOffset = 6
	}
	daysInMonth := time.Date(year, time.Month(month+1), 0, 0, 0, 0, 0, time.UTC).Day()

	week := make([]string, 0, 7)
	for i := 0; i < startOffset; i++ {
		week = append(week, "  ")
	}
	for d := 1; d <= daysInMonth; d++ {
		date := time.Date(year, time.Month(month), d, 0, 0, 0, 0, time.UTC)
		weekday := date.Weekday()
		isWeekend := weekday == time.Saturday || weekday == time.Sunday
		holidayName := getHoliday(month, d, country)
		var dayStr string
		if holidayName != "" {
			dayStr = colorize(fmt.Sprintf("%2d", d), red)
		} else if isWeekend {
			dayStr = colorize(fmt.Sprintf("%2d", d), green)
		} else {
			dayStr = fmt.Sprintf("%2d", d)
		}
		week = append(week, dayStr)
		if len(week) == 7 {
			fmt.Println(strings.Join(week, " "))
			week = make([]string, 0, 7)
		}
	}
	if len(week) > 0 {
		fmt.Println(strings.Join(week, " "))
	}

	// Праздники
	if m, ok := holidays[country][month]; ok && len(m) > 0 {
		fmt.Printf("\n%s\n", colorize("Праздники:", bold+yellow))
		// сортировка по дням
		days := make([]int, 0, len(m))
		for d := range m {
			days = append(days, d)
		}
		// простой пузырёк
		for i := 0; i < len(days); i++ {
			for j := i + 1; j < len(days); j++ {
				if days[i] > days[j] {
					days[i], days[j] = days[j], days[i]
				}
			}
		}
		for _, d := range days {
			fmt.Printf("  %2d – %s\n", d, m[d])
		}
	}
	fmt.Println()
}

func interactive() {
	reader := bufio.NewReader(os.Stdin)
	fmt.Println(colorize("Календарь с праздниками", bold+cyan))
	fmt.Print("Введите год (пусто для текущего): ")
	yearInput, _ := reader.ReadString('\n')
	yearInput = strings.TrimSpace(yearInput)
	var year int
	if yearInput == "" {
		year = time.Now().Year()
	} else {
		year, _ = strconv.Atoi(yearInput)
	}
	fmt.Print("Введите месяц (1-12, пусто для текущего): ")
	monthInput, _ := reader.ReadString('\n')
	monthInput = strings.TrimSpace(monthInput)
	var month int
	if monthInput == "" {
		month = int(time.Now().Month())
	} else {
		month, _ = strconv.Atoi(monthInput)
	}
	if month < 1 || month > 12 {
		fmt.Println("Неверный месяц")
		return
	}
	fmt.Print("Введите код страны (ru/us/by, по умолчанию ru): ")
	countryInput, _ := reader.ReadString('\n')
	country := strings.TrimSpace(countryInput)
	if country == "" {
		country = "ru"
	}
	if _, ok := holidays[country]; !ok {
		fmt.Printf("Страна %s не поддерживается, используем ru\n", country)
		country = "ru"
	}
	printCalendar(year, month, country)
}

func main() {
	args := os.Args[1:]
	if len(args) >= 2 {
		year, err1 := strconv.Atoi(args[0])
		month, err2 := strconv.Atoi(args[1])
		country := "ru"
		if len(args) >= 3 {
			country = args[2]
		}
		if err1 != nil || err2 != nil || month < 1 || month > 12 {
			fmt.Println("Неверный формат года или месяца")
			return
		}
		if _, ok := holidays[country]; !ok {
			fmt.Printf("Страна %s не поддерживается, используем ru\n", country)
			country = "ru"
		}
		printCalendar(year, month, country)
	} else {
		interactive()
	}
}
