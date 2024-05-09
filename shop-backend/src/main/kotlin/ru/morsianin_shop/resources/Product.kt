package ru.morsianin_shop.resources

import io.ktor.resources.Resource
import ru.morsianin_shop.model.ImageResponse

@Resource("/products")
class ProductRequest(
    val name: String? = null,
    val description: String? = null,
    val categoryId: Long? = null,
    val price: Long? = null,
    val quantity: Long? = null,
    val active: Boolean? = null,
) {
    @Resource("{id}")
    class Id(val parent: ProductRequest = ProductRequest(), val id: Long)
}