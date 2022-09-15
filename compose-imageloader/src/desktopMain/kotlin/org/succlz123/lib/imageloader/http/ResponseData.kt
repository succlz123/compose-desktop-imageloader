package org.succlz123.lib.imageloader.http

import com.jakewharton.disklrucache.DiskLruCache

class ResponseData(
    val contentType: String,
    val contentLength: Int = 0,
    val contentSnapshot: DiskLruCache.Snapshot
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