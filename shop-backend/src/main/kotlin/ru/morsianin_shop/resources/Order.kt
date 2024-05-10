package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItemResponse
import ru.morsianin_shop.model.OrderStatus

@Resource("/order")
data class OrderRequest(
    val userId: Long? = null,
    val items: List<OrderItemResponse>? = null,
    val status: OrderStatus? = null
)   {
    @Resource("{id}")
    data class Id(val parent: OrderRequest = OrderRequest(), val id: Long) {
        @Resource("status")
        data class Status(val parent: Id, val status: OrderStatus)

        @Resource("item")
        data class Item(val parent: Id) {
            @Resource("{itemId}")
            data class ItemId(val parent: Id, val itemId: Long)
        }
    }
}