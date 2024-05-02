$sep = " ";
$res_limit = 10;
$filtr = "()-+=,.\'/\\|[]{}`~?!<>:*&^%$#@";
$word_len_threshold = 1;

$splitstr = ($str) -> {
    return Unicode::SplitToList(Unicode::Fold(Unicode::RemoveAll($str, $filtr), "Russian" AS Language), $sep)};

$SearchTable = 
    select 
        search_word 
    from 
        (
        select 
            $splitstr("картошка шёл") as search_word
        ) 
    flatten by 
        search_word 
    where 
        Unicode::GetLength(search_word) > $word_len_threshold; 

$ref_table_list = 
    select 
        id,
        $splitstr(text) as list_text
    from 
        `search-levenstein`;

$ref_table_len = 
    select
        id,
        ListLength(list_text) as words_count
    from
        $ref_table_list;

$ref_table = 
    select 
        id, 
        list_text as source_word
    from
        $ref_table_list
    flatten by 
        list_text;

$comptable = select 
        id, 
        Unicode::LevensteinDistance(search_word, source_word) as distance,
        search_word
        -- source_word
    from 
        (
        select 
            * 
        from 
            $SearchTable as st 
        cross join 
            $ref_table as rt
        );

$result = 
    select
        id as id,
        avg(min_distance) as avg_distance,
    from
        (
        select
            id,
            min(distance) as min_distance
        from
            $comptable
        group by
            id, search_word
        )
    group by
        id;
        
select 
    rs.id,
    avg_distance,
    words_count,
from 
    $result as rs
inner join
    $ref_table_len as ln
on
    ln.id = rs.id
order by
    avg_distance asc,
    words_count asc,
    rs.id asc
limit
    $res_limit;

-- не та логика: среднее считается по всем комбинациям, но нужно выбрать пары слов запрос-источник с наименьшими дистанциями и по ним взять среднее
-- мб и не надо или только для подсказок:
-- select Unicode::Substring(source_word, 0, min_of(Unicode::GetLength(source_word), Unicode::GetLength(search_word))) as source_word_part, search_word from $compareTable;
-- сформировать таблицу комбинаций запрос-источник
-- хранить кеш по словам на стороне сервера в таблице с TTL
-- для каждого слова запроса проверять наличие в кеше, если его нет, то искать уже полноценно
-- если слов несколько, то возвращать пересечение?
-- спрашивать бек о подсказках только спустя скунду после того, как пользователь остановился?
-- мб проще и дешевле выделить поиск в отдельный контейнер и все-таки читать в память все названия
--    CASE
--        WHEN series_id = 1
--        THEN "IT Crowd"
--        ELSE "Other series"