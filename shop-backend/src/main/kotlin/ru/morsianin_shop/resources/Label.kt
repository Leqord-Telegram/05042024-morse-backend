package ru.morsianin_shop.resources

import io.ktor.resources.*

@Resource("/label")
class LabelRequest()   {
    @Resource("{id}")
    class Id(val parent: LabelRequest = LabelRequest(), val id: Long) {
    }
}