$sep = " ";
$res_limit = 10;
$search_word = "пень лес собака шёл картошка";
$filtr = "()-+=,.\'/\\|[]{}`~?!<>:*&^%$#@";
$word_len_threshold = 1;
$filter_distance_threshold = 4

$splitstr = ($str) -> {
    return Unicode::SplitToList(Unicode::Fold(Unicode::RemoveAll($str, $filtr), "Russian" AS Language), $sep)};

$search_table = 
    select 
        search_word 
    from 
        (
        select 
            $splitstr($search_word) as search_word
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
            $search_table as st 
        cross join 
            $ref_table as rt
        );

$result = 
    select
        id as id,
        cast(sum(min_distance) as float) / count(1) as sum_distance,
    from
        (
        select
            id,
            min(distance) as min_distance
        from
            (
            select
                *
            from
                $comptable
            where 
                distance < $filter_distance_threshold
            )
        group by
            id, search_word
        )
    group by
        id;
        
select 
    rs.id,
    sum_distance
from 
    $result as rs
order by
    sum_distance asc,
    rs.id asc
limit
    $res_limit;




-- теперь попробовать кешировать результаты для отдельных слов строки поиска
-- уже неплохо работает: слова с меньшим количеством дают большую дистанцию
-- проанализировать теоретически, если будет время
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
