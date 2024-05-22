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
    val category: CategoryResponse,
    val price: Long,
    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDate,
    val priceOld: Long?,
    val quantity: Long,
    val active: Boolean,
    val images: List<ImageResponse>
)

@Serializable
data class ProductNew(
    val name: String,
    val description: String,
    val categoryId: Long,
    val price: Long,
    val quantity: Long,
    val active: Boolean,
    val imageIds: List<Long>
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
