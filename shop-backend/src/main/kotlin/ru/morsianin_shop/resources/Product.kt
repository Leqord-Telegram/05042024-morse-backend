package ru.morsianin_shop.resources

import io.ktor.resources.Resource

@Resource("/product")
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