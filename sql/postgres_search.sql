-- SELECT regexp_replace('валыщва ыа "№*!?озфвы', '[^[:alpha:]\s]', '', 'g')
WITH search_words AS (
    SELECT unnest(string_to_array(regexp_replace('жопа член хуй залупа', '[^[:alpha:]\s]', '', 'g'), ' ')) AS search_word
),
product_words AS (
    SELECT
        t.id,
        unnest(string_to_array(regexp_replace(t.name, '[^[:alpha:]\s]', '', 'g'), ' ')) AS product_word
    FROM
        product AS t
),
distance_calc AS (
    SELECT
        p.id,
        s.search_word,
        p.product_word,
        levenshtein(lower(s.search_word), lower(p.product_word)) AS lev_distance
    FROM
        search_words s
    CROSS JOIN
        product_words p
),
min_distances AS (
    SELECT
        id,
        search_word,
        MIN(lev_distance) AS min_distance
    FROM
        distance_calc
    GROUP BY
        id, search_word
),
sum_distances AS (
    SELECT
        id,
        SUM(min_distance) AS total_distance
    FROM
        min_distances
    GROUP BY
        id
)
SELECT
    t.id,
    t.name,
    sd.total_distance
FROM
    product t
JOIN
    sum_distances sd ON t.id = sd.id
ORDER BY
    sd.total_distance ASC, t.id;
