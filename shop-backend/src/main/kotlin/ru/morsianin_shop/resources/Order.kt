package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItem
import ru.morsianin_shop.model.OrderStatus

@Resource("/orders")
class Orders(
    val userId: Long? = null,
    val items: List<OrderItem>? = null,
    val status: OrderStatus? = null
)   {
    @Resource("{id}")
    class Id(val parent: Orders = Orders(), val id: Long)
}