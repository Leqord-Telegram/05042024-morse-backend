package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.Order

@Resource("/users")
class Users(
    val name: String? = null,
    val admin: Boolean? = null,
    val cart: List<Order.Item>? = null,
)   {
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: Long) {
        @Resource("cart")
        class Cart(val parent: Id)
    }
}