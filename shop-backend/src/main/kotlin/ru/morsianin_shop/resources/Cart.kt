package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/cart")
data class Cart (
    val userId: Long,
    val items: List<CartItem>
) {
    @Resource("item")
    class CartItem(val parent: Id) {
        @Resource("{id}")
        class Id(val parent: CartItem, val id: Long)
    }
}
