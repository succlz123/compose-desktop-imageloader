package org.succlz123.lib.imageloader.http

import java.io.File

class ResponseData(
    val contentType: String,
    val contentLength: Int = 0,
    val contentSnapshot: File
) {

    var imageType: ImageType? = null

    init {
        imageType = when (contentType) {
            "image/png" -> ImageType.Png
            "image/jpeg", "image/jpg" -> ImageType.Jpg
            "image/webp" -> ImageType.WebP
            else -> null
        }
    }

    fun isSupportImage(): Boolean {
        return imageType != null
    }
}

enum class ImageType(val value: String) {
    Jpg("jpg"), Png("png"), WebP("webp")
}