package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.Order

@Resource("/orders")
class Orders(
    val userId: Long? = null,
    val items: List<Order.Item>? = null,
    val status: Order.Status? = null
)   {
    @Resource("{id}")
    class Id(val parent: Orders = Orders(), val id: Long)
}