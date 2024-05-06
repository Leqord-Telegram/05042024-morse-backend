package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/images")
class Images(
    val id: Long? = null,
    val link: String? = null,
)   {
    @Resource("{id}")
    class Id(val parent: Images = Images(), val id: Long)
}