package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/search")
class SearchRequest(
    val query: String? = null,
    val topN: Int = 20
)

