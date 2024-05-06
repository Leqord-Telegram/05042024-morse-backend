package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/categories")
class Categories(
    val name: String? = null
)   {
    @Resource("{id}")
    class Id(val parent: Categories = Categories(), val id: Long)
}