use std::fs::File;
use std::io::{self, BufRead, BufReader};
use std::time::Instant;

fn levenshtein(s1: &str, s2: &str) -> usize {
    let len1 = s1.chars().count();
    let len2 = s2.chars().count();

    // Специальные случаи
    if len1 == 0 {
        return len2;
    }
    if len2 == 0 {
        return len1;
    }

    // Используем вектор для хранения только двух строк матрицы расстояний
    let mut current_row = (0..=len2).collect::<Vec<_>>();
    let mut previous_row = vec![0; len2 + 1];

    for (i, c1) in s1.chars().enumerate() {
        // Свапаем строки
        std::mem::swap(&mut current_row, &mut previous_row);

        // Инициализация текущего ряда
        current_row[0] = i + 1;

        for (j, c2) in s2.chars().enumerate() {
            let cost = if c1 == c2 { 0 } else { 1 };

            // Вычисляем минимум из трех возможных операций
            current_row[j + 1] = std::cmp::min(
                std::cmp::min(
                    previous_row[j + 1] + 1,   // удаление
                    current_row[j] + 1         // вставка
                ),
                previous_row[j] + cost        // замена
            );
        }
    }

    // Расстояние Левенштейна находится в последнем элементе последнего ряда
    current_row[len2]
}


fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Открываем файл с русскими словами.
    let file = File::open("./russian.txt")?;
    let reader = BufReader::new(file);

    let mut dict: Vec<String> = Vec::new();

    dict.reserve(1000000);


    for line in reader.lines() {
        let line = line?;
        dict.push(line)
    }

    let stdin = io::stdin();
    loop {
        println!("Введите слово (или 'exit' для выхода):");
        let mut input = String::new();
        stdin.read_line(&mut input)?;

        if input.trim() == "exit" {
            break;
        }

        println!("Подсказки:");
        let start = Instant::now();
        for dict_entry in &dict {
            if levenshtein(&dict_entry, input.trim()) < 4 {
                println!("{}", dict_entry)
            }
        }
        let duration = start.elapsed();
        println!("Время выполнения: {:?}", duration);
    }

    Ok(())
}
