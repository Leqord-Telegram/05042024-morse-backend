use fst::{IntoStreamer, Set, SetBuilder};
use fst::automaton::Levenshtein;
use std::fs::File;
use std::io::{self, BufRead, BufReader};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Открываем файл с русскими словами.
    let file = File::open("./russian.txt")?;
    let reader = BufReader::new(file);

    // Считываем слова из файла.
    let mut set_builder = SetBuilder::memory();
    for line in reader.lines() {
        let line = line?;
        set_builder.insert(&line)?;
    }
    let set = set_builder.into_set();

    // Цикл для ввода пользователя и поиска подсказок.
    let stdin = io::stdin();
    loop {
        println!("Введите слово (или 'exit' для выхода):");
        let mut input = String::new();
        stdin.read_line(&mut input)?;

        // Обработка команды выхода.
        if input.trim() == "exit" {
            break;
        }

        println!("Подсказки:");
        for distance in 1..4 {
            println!("D = {}", distance);
            // Создаем запрос для поиска с использованием алгоритма Левенштейна.
            let lev = Levenshtein::new(&input.trim(), distance)?;

            // Применяем запрос и получаем результаты.
            let stream = set.search(lev).into_stream();
            let keys = stream.into_strs()?;
            
            for key in keys {
                println!("{}", key);
            }
        }
    }

    Ok(())
}
