package ru.morsianin_shop.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class OrderResponse(
    val id: Long,
    val items: List<OrderItemResponse>,
    val status: OrderStatus,
    val userName: String,
    val description: String,
    val phone: String?,
    val shipment: OrderShipment,
    @Serializable(with = LocalDateTimeSerializer::class)
    val shipmentDateTime: LocalDateTime?,
    val shipmentAddress: String?,
)

@Serializable
data class OrderItemResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int
)

@Serializable
data class OrderNew(
    val items: List<OrderItemNew>,
    val userName: String,
    val description: String,
    val phone: String,
    val shipment: OrderShipment,
    @Serializable(with = LocalDateTimeSerializer::class)
    val shipmentDateTime: LocalDateTime,
    val shipmentAddress: String,
)

@Serializable
data class OrderItemNew(
    val productId: Long,
    val quantity: Int
)

@Serializable
data class OrderItemChanged(
    val quantity: Int
)

@Serializable
enum class OrderStatus {
    FAILED,
    CANCELED,
    PENDING,
    SHIPPING,
    ARRIVED,
    FINISHED
}

@Serializable
enum class OrderShipment {
    Pickup,
    Courier
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}