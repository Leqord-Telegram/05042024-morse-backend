package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/cart")
data class CartRequest (
    val userId: Long? = null,
) {
    @Resource("{id}")
    class Id(val parent: CartRequest = CartRequest(), val id: Long)
}
