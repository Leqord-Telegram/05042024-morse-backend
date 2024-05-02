$sep = " ";
$filtr = "()-+=,.\'/\\|[]{}`~?!<>:*&^%$#@";

$splitstr = ($str) -> {
    return Unicode::SplitToList(Unicode::Fold(Unicode::RemoveAll($str, $filtr), "Russian" AS Language), $sep)
};

$strSearch = "картошка и шёл";
$SearchSplit = $splitstr($strSearch);
$SearchTable = select search_word from (select $SearchSplit  as search_word) flatten by search_word where Unicode::GetLength(search_word) > 1; 

$orsplit = select id, $splitstr(text) as list_text from `search-levenstein`;

$RefTable = select id, list_text as source_word from $orsplit flatten by list_text;

$compareTable = 

select 
    id, 
    AVG(distance) as weighted_distance
from 
    (
    select 
        id, 
        Unicode::LevensteinDistance(search_word, source_word) as distance 
    from 
        (
        select 
            * 
        from 
            $SearchTable as st 
        cross join 
            $RefTable as rt
        )
    )
group by
    id
order by
    weighted_distance desc
;


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