package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderDTO
import ru.morsianin_shop.model.OrderItemDTO
import ru.morsianin_shop.model.OrderStatusDTO

@Resource("/orders")
class Orders(
    val userId: Long? = null,
    val items: List<OrderItemDTO>? = null,
    val status: OrderStatusDTO? = null
)   {
    @Resource("{id}")
    class Id(val parent: Orders = Orders(), val id: Long)
}