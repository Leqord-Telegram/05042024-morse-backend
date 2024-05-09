package ru.morsianin_shop.resources

import io.ktor.resources.*
import ru.morsianin_shop.model.OrderItemResponse
import ru.morsianin_shop.model.OrderStatus

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

        // Для администратора доступ ко всем предшествующим id,
        // а для обычного пользователя - только к своему
        @Resource("order")
        class OrderRequest(
            val userId: Long? = null,
            val items: List<OrderItemResponse>? = null,
            val status: OrderStatus? = null
        )   {
            @Resource("{id}")
            class Id(val parent: OrderRequest = OrderRequest(), val id: Long)
        }
    }

}