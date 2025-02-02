package ru.morsianin_shop.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val category: List<CategoryResponse>,
    val price: Long,
    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDate,
    val priceOld: Long?,
    val quantity: Long,
    val active: Boolean,
    val inStock: Boolean,
    val images: List<ImageResponse>,
    val labels: List<LabelResponse>,
    val unit: String,
    val priority: List<ProductCategoryPriorityResponse>, // TODO: не отдавать просто так, только с флагом
)

@Serializable
data class ProductCategoryPriorityResponse(
    val categoryId: Long,
    val priority: Long?,
)

@Serializable
data class ProductCategoryPriorityUpdate(
    val productId: Long,
    val priority: Long?,
)

@Serializable
data class ProductNotificationNew(
    val phone: String?,
    val name: String?,
)

@Serializable
data class ProductNew(
    val name: String,
    val description: String,
    val categoriesId: List<Long>,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val inStock: Boolean,
    val imageIds: List<Long>,
    val labelIds: List<Long>,
    val unit: String,
)

@Serializable
enum class ProductSort {
    PriceAsc,
    PriceDesc,
    QuantityAsc,
    QuantityDesc,
    NameAsc,
    NameDesc,
    CreatedAsc,
    CreatedDesc,
    IdAsc,
    PriorityAsc,
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}
