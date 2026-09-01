# calendar.rb
# Календарь с праздниками на Ruby

require 'date'

# ANSI-цвета
RESET = "\033[0m"
RED = "\033[91m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
CYAN = "\033[96m"
BOLD = "\033[1m"

def colorize(text, color)
  "#{color}#{text}#{RESET}"
end

# Праздники
HOLIDAYS = {
  'ru' => {
    1 => {1 => 'Новый год', 7 => 'Рождество Христово'},
    2 => {23 => 'День защитника Отечества'},
    3 => {8 => 'Международный женский день'},
    5 => {1 => 'Праздник Весны и Труда', 9 => 'День Победы'},
    6 => {12 => 'День России'},
    11 => {4 => 'День народного единства'},
  },
  'us' => {
    1 => {1 => "New Year's Day"},
    7 => {4 => 'Independence Day'},
    11 => {11 => 'Veterans Day'},
    12 => {25 => 'Christmas Day'},
  },
  'by' => {
    1 => {1 => 'Новы год', 7 => 'Каляды'},
    3 => {8 => 'Міжнародны жаночы дзень'},
    5 => {1 => 'Дзень працы', 9 => 'Дзень Перамогі'},
    7 => {3 => 'Дзень Незалежнасці'},
    11 => {7 => 'Дзень Кастрычніцкай рэвалюцыі'},
  }
}

def get_holiday(month, day, country)
  HOLIDAYS[country]&.dig(month, day)
end

def print_calendar(year, month, country)
  month_names = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
                 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь']
  puts "\n#{colorize("#{month_names[month-1]} #{year}", BOLD + CYAN)}"
  puts colorize('Пн Вт Ср Чт Пт Сб Вс', BOLD)
  puts '---------------------'

  first_day = Date.new(year, month, 1)
  start_offset = (first_day.wday + 6) % 7 # 0-пн, 6-вс
  days_in_month = Date.new(year, month, -1).day

  week = []
  start_offset.times { week << '  ' }
  (1..days_in_month).each do |d|
    date = Date.new(year, month, d)
    weekday = (date.wday + 6) % 7
    is_weekend = (weekday == 5 || weekday == 6)
    holiday = get_holiday(month, d, country)
    day_str = if holiday
                colorize(sprintf('%2d', d), RED)
              elsif is_weekend
                colorize(sprintf('%2d', d), GREEN)
              else
                sprintf('%2d', d)
              end
    week << day_str
    if week.length == 7
      puts week.join(' ')
      week = []
    end
  end
  puts week.join(' ') unless week.empty?

  # Праздники
  if HOLIDAYS[country]&.key?(month)
    puts "\n#{colorize('Праздники:', BOLD + YELLOW)}"
    HOLIDAYS[country][month].keys.sort.each do |d|
      puts "  #{sprintf('%2d', d)} – #{HOLIDAYS[country][month][d]}"
    end
  end
  puts
end

def interactive
  puts colorize('Календарь с праздниками', BOLD + CYAN)
  print 'Введите год (пусто для текущего): '
  year_input = gets.chomp
  year = year_input.empty? ? Date.today.year : year_input.to_i
  print 'Введите месяц (1-12, пусто для текущего): '
  month_input = gets.chomp
  month = month_input.empty? ? Date.today.month : month_input.to_i
  if month < 1 || month > 12
    puts 'Неверный месяц'
    return
  end
  print 'Введите код страны (ru/us/by, по умолчанию ru): '
  country = gets.chomp
  country = 'ru' if country.empty?
  unless HOLIDAYS.key?(country)
    puts "Страна #{country} не поддерживается, используем ru"
    country = 'ru'
  end
  print_calendar(year, month, country)
end

if ARGV.length >= 2
  year = ARGV[0].to_i
  month = ARGV[1].to_i
  country = ARGV[2] || 'ru'
  if month < 1 || month > 12
    puts 'Месяц должен быть от 1 до 12'
    exit 1
  end
  unless HOLIDAYS.key?(country)
    puts "Страна #{country} не поддерживается, используем ru"
    country = 'ru'
  end
  print_calendar(year, month, country)
else
  interactive
end
