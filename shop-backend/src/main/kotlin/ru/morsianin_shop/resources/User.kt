package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItemResponse

@Resource("/users")
class UserRequest(
    val name: String? = null,
    val admin: Boolean? = null,
    val cart: List<OrderItemResponse>? = null,
)   {
    @Resource("{id}")
    class Id(val parent: UserRequest = UserRequest(), val id: Long) {
        @Resource("cart")
        class Cart(val parent: Id)
    }
}