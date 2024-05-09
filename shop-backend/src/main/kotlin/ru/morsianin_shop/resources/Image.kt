package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/images")
class ImageRequest(
    val id: Long? = null,
    val link: String? = null,
)   {
    @Resource("{id}")
    class Id(val parent: ImageRequest = ImageRequest(), val id: Long)
}