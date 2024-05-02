$separator = " ";
$strAsplit = Unicode::SplitToList(Unicode::Fold("шоссеандра шла"), $separator);


select source_str from (
    select $strAsplit  as source_str
) flatten by source_str;

-- сформировать таблицу комбинаций запрос-источник
-- хранить кеш по словам на стороне сервера в таблице с TTL
-- для каждого слова запроса проверять наличие в кеше, если его нет, то искать уже полноценно
-- если слов несколько, то возвращать пересечение?
-- спрашивать бек о подсказках только спустя скунду после того, как пользователь остановился?
    CASE
        WHEN series_id = 1
        THEN "IT Crowd"
        ELSE "Other series"