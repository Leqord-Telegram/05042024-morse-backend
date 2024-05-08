package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderDTO

@Resource("/users")
class Users(
    val name: String? = null,
    val admin: Boolean? = null,
    val cart: List<OrderDTO.Item>? = null,
)   {
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: Long) {
        @Resource("cart")
        class Cart(val parent: Id)
    }
}