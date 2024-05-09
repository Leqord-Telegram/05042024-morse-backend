package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItemResponse
import ru.morsianin_shop.model.OrderStatus

@Resource("/users")
class UserRequest(
    val name: String? = null,
    val admin: Boolean? = null,
    val cart: List<OrderItemResponse>? = null,
)