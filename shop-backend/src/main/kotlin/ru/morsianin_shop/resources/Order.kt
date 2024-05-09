package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItemResponse
import ru.morsianin_shop.model.OrderStatus

@Resource("/orders")
class OrderRequest(
    val userId: Long? = null,
    val items: List<OrderItemResponse>? = null,
    val status: OrderStatus? = null
)   {
    @Resource("{id}")
    class Id(val parent: OrderRequest = OrderRequest(), val id: Long)
}