package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/kv")
data class KVRequest (
    val userId: Long? = null,
) {
    @Resource("cancel_threshold")
    class cancelThreshold(val parent: KVRequest = KVRequest())

    @Resource("description")
    class description(val parent: KVRequest = KVRequest())
}