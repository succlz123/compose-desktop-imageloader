package org.succlz123.lib.imageloader.http

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.newFixedThreadPoolContext
import org.succlz123.lib.imageloader.cache.core.DiskLruCache
import org.succlz123.lib.imageloader.utils.ImageLoaderLogger
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpConnectionClient(
    private val threadPool: CoroutineDispatcher = newFixedThreadPoolContext(2, "Downloader"),
) {

    private val job = Job()

    var diskLruCache: DiskLruCache? = null

    fun dispatcher(): CoroutineDispatcher {
        return threadPool
    }

    suspend fun pullImage(url: String, key: String): ResponseData? {
        var conn: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            conn = url.openConnection(null)
            conn.requestMethod = "GET"
            if (conn.responseCode != 200) {
                debugLog("Response status code is (${conn.responseCode})!")
                return null
            }
            val contentTypeString = conn.contentType
            if (contentTypeString == null) {
                debugLog("Content-type is null!")
                return null
            }
            val contentLength = conn.contentLength
            if (contentLength <= 0) {
                debugLog("Content length is null!")
                return null
            }
            inputStream = conn.inputStream

            diskLruCache?.getOrPut(key) { cacheFile ->
                try {
                    val outputStream = cacheFile.outputStream()
                    inputStream.copyTo(outputStream)
                    try {
                        outputStream.close()
                    } catch (e: Exception) {
                    }
                    true // Caching succeeded - Save the file
                } catch (ex: IOException) {
                    false
                }
            }
            val snapshot = diskLruCache?.get(key) ?: return null
            return ResponseData(contentTypeString, contentLength, snapshot)
        } catch (error: Throwable) {
            error.printStackTrace()
            return null
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }
            conn?.disconnect()
        }
    }

    fun close() {
        job.cancel()
    }

    private fun debugLog(msg: String) {
        ImageLoaderLogger.debugLog("Thread: ${Thread.currentThread().name}, $msg")
    }

    @Throws(IOException::class)
    private fun String.openConnection(
        ua: String?, connectTimeout: Int = 6000, readTimeout: Int = 6000
    ): HttpURLConnection {
        val connection = URL(this).openConnection() as HttpURLConnection
        if (ua != null) {
            connection.setRequestProperty("User-Agent", ua)
        }
        connection.connectTimeout = connectTimeout
        connection.readTimeout = readTimeout
        return connection
    }
}