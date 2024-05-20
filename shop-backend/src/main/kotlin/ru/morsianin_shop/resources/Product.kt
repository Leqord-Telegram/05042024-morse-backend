package ru.morsianin_shop.resources

import io.ktor.resources.Resource
import ru.morsianin_shop.model.ProductSort

@Resource("/product")
class ProductRequest(
    val name: String? = null,
    val description: String? = null,
    val categoryId: Long? = null,
    val price: Long? = null,
    val quantity: Long? = null,
    val active: Boolean? = true,
    val sort: ProductSort = ProductSort.IdAsc,
    val offset: Long = 0,
    val limit: Int = 100,
) {
    @Resource("{id}")
    class Id(val id: Long)

    @Resource("total")
    class Total()
}