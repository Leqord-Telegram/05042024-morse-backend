package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderDTO

@Resource("/orders")
class Orders(
    val userId: Long? = null,
    val items: List<OrderDTO.Item>? = null,
    val status: OrderDTO.Status? = null
)   {
    @Resource("{id}")
    class Id(val parent: Orders = Orders(), val id: Long)
}