import psycopg2
import random
import string

# Подключаемся к базе данных
conn = psycopg2.connect(
    host="127.0.0.1",
    database="shop",
    user="postgresuser",
    password="adminpass"
)

# Создаём курсор для выполнения SQL-запросов
cur = conn.cursor()

# Предопределённые значения
words = ["хуй", "жопа", "член", "банан", "залупа", "пизда", "анус", "говно", "псина", "горбатая"]
categories = [1,]  # Подставьте сюда ваши реальные категории

# Функция для создания случайных строк
def random_text(min_words=4, max_words=10):
    return ' '.join(random.choice(words) for _ in range(random.randint(min_words, max_words)))

# Количество записей для вставки
num_records = 10000  # Замените на ваше число

# Создаём и вставляем случайные записи
for _ in range(num_records):
    name = random_text()
    description = random_text()
    category_id = random.choice(categories)
    price = random.randint(100, 10000)  # Подставьте желаемый диапазон цен
    quantity = random.randint(1, 100)   # Подставьте желаемый диапазон количества
    active = bool(random.getrandbits(1))

    # Выполняем вставку
    cur.execute("""
        INSERT INTO product (name, description, category_id, price, quantity, active)
        VALUES (%s, %s, %s, %s, %s, %s)
    """, (name, description, category_id, price, quantity, active))

# Подтверждаем транзакцию
conn.commit()

# Закрываем соединение
cur.close()
conn.close()

