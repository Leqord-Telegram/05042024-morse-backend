package ru.morsianin_shop.resources

import io.ktor.resources.Resource
import ru.morsianin_shop.model.Image

@Resource("/products")
class Products(
    val name: String? = null,
    val description: String? = null,
    val categoryId: Long? = null,
    val price: Long? = null,
    val quantity: Long? = null,
    val active: Boolean? = null,
    val images: List<Image>? = null
) {
    @Resource("{id}")
    class Id(val parent: Products = Products(), val id: Long)
}