-- select Unicode::LevensteinDistance("шоссеандра", "шоф") as distance
$separator = " ";

$strRef = "шоссеандра   шла";
$RefSplit = Unicode::SplitToList(Unicode::Fold($strRef), $separator);
$RefTable = select source_str from (select $RefSplit  as source_str) flatten by source_str; 

$strSearch = "шоссе";
$SearchSplit = Unicode::SplitToList(Unicode::Fold($strRef), $separator);
$SearchTable = select search_str from (select $SearchSplit  as search_str) flatten by search_str; 

select $RefStable;

-- сформировать таблицу комбинаций запрос-источник
-- хранить кеш по словам на стороне сервера в таблице с TTL
-- для каждого слова запроса проверять наличие в кеше, если его нет, то искать уже полноценно
-- если слов несколько, то возвращать пересечение?
-- спрашивать бек о подсказках только спустя скунду после того, как пользователь остановился?
-- мб проще и дешевле выделить поиск в отдельный контейнер и все-таки читать в память все названия
    CASE
        WHEN series_id = 1
        THEN "IT Crowd"
        ELSE "Other series"