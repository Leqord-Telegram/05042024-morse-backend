package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/cart")
data class CartRequest (
    val userId: Long,
    val items: List<CartItemRequest>
) {
    @Resource("item")
    class CartItemRequest(val parent: Id) {
        @Resource("{id}")
        class Id(val parent: CartItemRequest, val id: Long)
    }
}
