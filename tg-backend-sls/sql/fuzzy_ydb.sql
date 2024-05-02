$separator = " ";
$strAsplit = Unicode::SplitToList(Unicode::Fold("шоссеандра шла"), $separator);


select source_str from (
    select $strAsplit  as source_str
) flatten by source_str;

-- сформировать таблицу комбинаций запрос-источник
-- хранить кеш по словам на стороне сервера в таблице с TTL