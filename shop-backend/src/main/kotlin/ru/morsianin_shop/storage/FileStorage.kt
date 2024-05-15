package ru.morsianin_shop.storage

import java.io.File

object FileStorage {
    fun saveFile(filename: String, content: ByteArray) {
        val saveDir = System.getenv("IMAGES_DIR")?: "./images"

        File("$saveDir/$filename").writeBytes(content)
    }

    fun getFile(filename: String): File {
        val saveDir = System.getenv("IMAGES_DIR")?: "./images"
        return File("$saveDir/$filename")
    }
}