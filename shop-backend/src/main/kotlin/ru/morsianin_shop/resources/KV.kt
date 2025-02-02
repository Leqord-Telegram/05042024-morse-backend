package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/kv")
data class KVRequest (
    val userId: Long? = null,
) {
    @Resource("cancel_threshold")
    class cancelThreshold(val parent: KVRequest = KVRequest())

    @Resource("about_us")
    class aboutUs(val parent: KVRequest = KVRequest())

    @Resource("phone")
    class phone(val parent: KVRequest = KVRequest())

    @Resource("whatsapp")
    class whatsapp(val parent: KVRequest = KVRequest())

    @Resource("telegram")
    class telegram(val parent: KVRequest = KVRequest())

    @Resource("default_category")
    class defaultCategory(val parent: KVRequest = KVRequest())
}