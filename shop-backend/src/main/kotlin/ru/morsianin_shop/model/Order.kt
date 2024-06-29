package ru.morsianin_shop.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.sql.SortOrder
import ru.morsianin_shop.storage.StoredOrder
import ru.morsianin_shop.storage.StoredProducts
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

fun printOrderMessage(order: OrderResponse, userName: String): String {
    val shipname = when (order.shipment) {
        OrderShipment.Pickup -> "САМОВЫВОЗ"
        OrderShipment.Courier -> "КУРЬЕР"
    }

    val sb = StringBuilder()

    sb.append("""#Создан заказ ${order.id}
        |Заказчик: ${order.userName}
        |Телефон: ${order.phone?: "НЕ УКАЗАН"}
        |Доставка: $shipname
        |Время доставки: ${order.shipmentDateTime?: "НЕ УКАЗАНО"}
        |Адрес: ${order.shipmentAddress?: "НЕ УКАЗАН"}
        |Комментарий: ${order.description}
        |Telegram: @${userName}
        |Состав заказа:
    """.trimMargin())

    for (item in order.items) {
        sb.append("✧ ${item.product.name} ${item.quantity}шт. на ${item.quantity * item.product.price}₽\n")
    }

    return sb.toString()
}