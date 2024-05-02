$stringA = "шоссеандра шла";
$separator = " ";

select distance from (
    select Unicode::SplitToList(Unicode::Fold($stringA), $separator) as distance
) flatten by distance;