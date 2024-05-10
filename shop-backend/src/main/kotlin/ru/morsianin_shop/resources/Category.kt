package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/category")
class CategoryRequest(
    val name: String? = null
)   {
    @Resource("{id}")
    class Id(val parent: CategoryRequest = CategoryRequest(), val id: Long)
}