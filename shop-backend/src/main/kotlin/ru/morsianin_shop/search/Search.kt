package ru.morsianin_shop.search

import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import me.xdrop.fuzzywuzzy.FuzzySearch
import ru.morsianin_shop.storage.DatabaseStorage.dbQuery
import ru.morsianin_shop.storage.StoredProduct
import ru.morsianin_shop.storage.StoredProducts
import java.util.concurrent.TimeUnit

object SearchLevenshtein {

    private val productCache = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<String, List<SearchItem>>()

    suspend fun getProducts(): List<SearchItem> {
        return withContext(Dispatchers.Default) {
            val cachedProducts = productCache.getIfPresent("products")
            if (cachedProducts != null) {
                cachedProducts
            } else {
                val products = dbQuery {
                    StoredProduct.find {
                        StoredProducts.active eq true
                        StoredProducts.enabled eq true
                    }.map { storedProduct ->
                        SearchItem(
                            id = storedProduct.id.value,
                            content = storedProduct.name,
                            type = SearchItemType.ProductName
                        )
                    }
                }
                productCache.put("products", products)
                products
            }
        }
    }

    suspend fun findTopBestMatchingProductsSuggestions(request: String, topN: Int): List<SearchResult> {
        return withContext(Dispatchers.Default) {
            getProducts().map { item ->
                SearchResult(
                    id =  item.id,
                    score = FuzzySearch.tokenSetPartialRatio(request, item.content),
                    type = item.type,
                    content = item.content
                )
            }.sortedByDescending { it.score }.take(topN)
        }
    }
}

@Serializable
data class SearchItem(
    val id: Long,
    val type: SearchItemType,
    val content: String,
)

@Serializable
data class SearchResult(
    val id: Long,
    val score: Int,
    val content: String,
    val type: SearchItemType,
)

@Serializable
enum class SearchItemType {
    ProductName,
}

