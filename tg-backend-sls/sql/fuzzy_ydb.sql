-- select Unicode::LevensteinDistance("шоссеандра", "шоф") as distance
$defsep = " ";

$splitstr = ($str, $sep) -> {
    return Unicode::SplitToList(Unicode::Fold($str), $sep)
};

$strRef = "шоссеандра   шла";
$RefSplit = $splitstr($strRef, $defsep);
$RefTable = select source_word from (select $RefSplit  as source_word) flatten by source_word; 

$strSearch = "шоссе шоа";
$SearchSplit = $splitstr($strSearch, $defsep);
$SearchTable = select search_word from (select $SearchSplit  as search_word) flatten by search_word; 

$compareTable = select * from $SearchTable as st cross join $RefTable as rt;
$min_length = select min_of(Unicode::GetLength(search_word), Unicode::GetLength(source_word)) as min_length from $compareTable;

select Unicode::Substring(source_word, 0, min_of(Unicode::GetLength(source_word), Unicode::GetLength(search_word))) as source_word_part, search_word from $compareTable;

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