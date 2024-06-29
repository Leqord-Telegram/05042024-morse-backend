package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderStatus

@Resource("/order")
data class OrderRequest(
    val userId: Long? = null,
    val status: OrderStatus? = null
)   {
    @Resource("{id}")
    data class Id(val parent: OrderRequest = OrderRequest(), val id: Long) {

        @Resource("status")
        data class Status(val parent: Id)

        @Resource("cancel")
        data class Cancel(val parent: Id)

        @Resource("item")
        data class Item(val parent: Id)
    }
}