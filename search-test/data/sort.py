import os
import heapq

def sort_and_save_part(data, index):
    """Сортирует часть данных и сохраняет их во временный файл."""
    data.sort()
    filename = f"temp_file_{index}.txt"
    with open(filename, 'w', encoding='utf-8') as f:
        f.write('\n'.join(data))
    return filename

def merge_files(files):
    """Слияние всех временных файлов в один финальный отсортированный файл."""
    with open('russian.txt', 'w', encoding='utf-8') as out:
        # Используем heapq для слияния, сохраняя порядок
        sorted_file_iters = [
            open(file, 'r', encoding='utf-8') for file in files
        ]
        sorted_iter = heapq.merge(*(sorted_file_iters), key=lambda x: x.strip())
        for line in sorted_iter:
            out.write(line)
        for f in sorted_file_iters:
            f.close()
            os.remove(f.name)  # Удаляем временные файлы после слияния

def sort_large_file(filename, max_lines=100000):
    """Сортирует большой файл, разбив его на части и затем сливая отсортированные части."""
    part = []
    part_index = 0
    files = []
    with open(filename, 'r', encoding='utf-8') as f:
        for line in f:
            part.append(line.strip())
            if len(part) >= max_lines:
                temp_filename = sort_and_save_part(part, part_index)
                files.append(temp_filename)
                part_index += 1
                part = []
        if part:
            temp_filename = sort_and_save_part(part, part_index)
            files.append(temp_filename)
    
    merge_files(files)

# Использование функции для сортировки файла
sort_large_file('russian_unsorted.txt')

