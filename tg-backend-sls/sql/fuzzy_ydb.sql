$sep = " ";
$filtr = "()-+=,.\'/\\|[]{}`~?!<>:*&^%$#@";

$splitstr = ($str) -> {
    return Unicode::SplitToList(Unicode::Fold(Unicode::RemoveAll($str, $filtr), "Russian" AS Language), $sep)
};

$strSearch = "картошка шёл";
$SearchSplit = $splitstr($strSearch);
$SearchTable = select search_word from (select $SearchSplit  as search_word) flatten by search_word where Unicode::GetLength(search_word) > 1; 

$orsplit = select id, $splitstr(text) as list_text from `search-levenstein`;

$RefTable = select id, list_text as source_word from $orsplit flatten by list_text;

$comptable = select 
        id, 
        Unicode::LevensteinDistance(search_word, source_word) as distance,
        search_word,
        source_word
    from 
        (
        select 
            * 
        from 
            $SearchTable as st 
        cross join 
            $RefTable as rt
        );

$result = select 
    id, 
    AVG(distance) as weighted_distance,
    SUM(distance) as sum_distance,
    COUNT(1) as number,
from 
    $comptable
group by
    id
;

select
    id,
    avg(min_distance) as avg_distance
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
    id
order by
    avg_distance asc
;


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