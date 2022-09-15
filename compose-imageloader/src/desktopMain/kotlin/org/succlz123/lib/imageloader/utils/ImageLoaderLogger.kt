package org.succlz123.lib.imageloader.utils

object ImageLoaderLogger {
    private const val TAG = "ImageLoaderLogger"

    var isDebug: Boolean = false

    fun debugLog(msg: String) {
        if (isDebug) {
            println("$TAG: $msg")
        }
    }
}