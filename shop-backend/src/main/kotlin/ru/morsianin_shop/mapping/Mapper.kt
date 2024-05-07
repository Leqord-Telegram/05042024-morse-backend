package ru.morsianin_shop.mapping

interface Mapper<SRC, DST> {
    fun transform(data: SRC): DST
}