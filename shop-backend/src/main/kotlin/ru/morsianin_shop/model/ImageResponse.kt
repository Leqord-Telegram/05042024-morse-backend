package ru.morsianin_shop.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val id: Long,
    val url: String,
    val content: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageResponse

        if (id != other.id) return false
        if (url != other.url) return false
        if (content != null) {
            if (other.content == null) return false
            if (!content.contentEquals(other.content)) return false
        } else if (other.content != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (content?.contentHashCode() ?: 0)
        return result
    }
}