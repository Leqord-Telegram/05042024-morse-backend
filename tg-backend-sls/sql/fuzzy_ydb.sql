$separator = " ";
$strRef = "шоссеандра шла";
$RefSplit = Unicode::SplitToList(Unicode::Fold($strRef), $separator);
$RefSTable = select $RefSplit  as source_str;


select source_str from $RefSTable flatten by source_str;

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