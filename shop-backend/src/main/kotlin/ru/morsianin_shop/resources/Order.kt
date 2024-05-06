package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.Order

@Resource("/orders")
class Orders(
    val userId: Long? = null,
    val items: List<Order.OrderItem>? = null,
    val status: Order.OrderStatus? = null
)   {
    @Resource("{id}")
    class Id(val parent: Orders = Orders(), val id: Long)
}