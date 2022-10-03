package org.succlz123.lib.imageloader.core

import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.*
import org.succlz123.lib.imageloader.cache.LruUtil
import org.succlz123.lib.imageloader.cache.MemoryCache
import org.succlz123.lib.imageloader.cache.core.DiskLruCache
import org.succlz123.lib.imageloader.http.HttpConnectionClient
import org.succlz123.lib.imageloader.transform.ITransformation
import org.succlz123.lib.imageloader.utils.ImageLoaderLogger
import java.io.File
import java.io.IOException

enum class SaveStrategy {
    Original, Transformed
}

val ImageInLoading = ImageResponse(null, null, true)

internal fun List<ITransformation>?.transformationKey(): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    return this.joinToString("-") { it.tag() }
}

class ImageLoader(
    maxMemoryCacheSize: Long, maxDiskCacheSize: Long, rootDirectory: File
) {

    companion object {
        const val CACHE_DEFAULT_MEMORY_SIZE = 1024 * 1024 * 300L
        const val CACHE_DEFAULT_DISK_SIZE = 1024 * 1024 * 100L
        val USER_DIR = File(System.getProperty("user.dir"))

        @Volatile
        var instance: ImageLoader? = null

        fun configuration(
            maxMemoryCacheSize: Long = CACHE_DEFAULT_MEMORY_SIZE,
            maxDiskCacheSize: Long = CACHE_DEFAULT_DISK_SIZE,
            rootDirectory: File = USER_DIR
        ) {
            instance = ImageLoader(maxMemoryCacheSize, maxDiskCacheSize, rootDirectory)
        }

        fun instance(): ImageLoader {
            val i = instance
            return if (i == null) {
                synchronized(ImageLoader::class.java) {
                    instance ?: ImageLoader(
                        CACHE_DEFAULT_MEMORY_SIZE, CACHE_DEFAULT_DISK_SIZE, USER_DIR
                    ).apply {
                        instance = this
                    }
                }
            } else {
                return i
            }
        }
    }

    private val job = SupervisorJob()

    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "caching-image-loader")

    private val scope = CoroutineScope(job)

    private var diskLruCache: DiskLruCache? = null

    private var memoryLruCache: MemoryCache

    private var imageCacheDir: File

    private val client: HttpConnectionClient

    private val loadingImageMap = HashMap<String, Boolean>()

    init {
        imageCacheDir = File(rootDirectory, "imageCache")
        if (!imageCacheDir.exists()) {
            if (!imageCacheDir.mkdirs()) {
                throw IllegalStateException("Could not create image cache directory: ${imageCacheDir.absolutePath}")
            }
        }
        memoryLruCache = MemoryCache(maxMemoryCacheSize)
        client = HttpConnectionClient()

        scope.launch {
            diskLruCache = DiskLruCache.open(directory = imageCacheDir, maxSize = maxDiskCacheSize)
            client.diskLruCache = diskLruCache
        }
    }

    private fun debugLog(msg: String) {
        ImageLoaderLogger.debugLog("Thread: ${Thread.currentThread().name}, $msg")
    }

    fun newRequest(): Request {
        return Request()
    }

    private suspend fun runRequest(request: Request): ImageResponse {
        val loadFile = request.file
        if (loadFile != null && loadFile.exists()) {
            return runFileLoad(loadFile, request.transformers)
        }
        return runUrlLoad(request)
    }

    private suspend fun runFileLoad(file: File, transformers: MutableList<ITransformation>): ImageResponse {
        return scope.async(dispatcher) {
            val key = LruUtil.hashKey(file.absolutePath) + transformers.transformationKey()
            val hasCache = memoryLruCache.getBitmap(key)
            if (hasCache != null) {
                ImageResponse(hasCache.toBitmapPainter(), null)
            } else {
                try {
                    var imageBitmap = file.inputStream().buffered().use(::loadImageBitmap)
                    for (transformer in transformers) {
                        imageBitmap = transformer.transform(imageBitmap)
                    }
                    memoryLruCache.putBitmap(key, imageBitmap)
                    ImageResponse(imageBitmap.toBitmapPainter(), null)
                } catch (e: Exception) {
                    ImageResponse(null, e)
                }
            }
        }.await()
    }

    private suspend fun runUrlLoad(request: Request): ImageResponse {
        val url = request.url
        if (url.isNullOrEmpty()) {
            debugLog("onError - Url is null or empty!")
            return ImageResponse(null, NullPointerException("Url is null or empty!"))
        }
        val getJob = scope.async(dispatcher) {
            val diskKey = LruUtil.hashKey(url)
            val memoryKey = diskKey + request.transformers.transformationKey()
            val memoryImage = memoryLruCache.getBitmap(memoryKey)
            if (memoryImage != null) {
                debugLog("onSuccess - from: memory")
                return@async ImageResponse(memoryImage.toBitmapPainter(), null)
            }

            if (loadingImageMap[diskKey] == true) {
                debugLog("onLoading")
                return@async ImageResponse(null, null, true)
            }
            try {
                val cacheFile = try {
                    diskLruCache?.get(diskKey)
                } catch (e: IOException) {
                    null
                }
                if (cacheFile == null) {
                    debugLog("pull ($url)")
                    loadingImageMap[diskKey] = true
                    val data = scope.async(client.dispatcher()) {
                        client.pullImage(url, diskKey)
                    }.await()
                    val newFetchedCache = data?.contentSnapshot
                    if (newFetchedCache == null) {
                        debugLog("onError")
                        loadingImageMap[diskKey] = false
                        return@async ImageResponse(null, NullPointerException("Can't find the local image snapshot"))
                    } else {
                        var imageBitmap = loadImageBitmap(newFetchedCache.inputStream())
                        if (diskKey != memoryKey) {
                            for (transformer in request.transformers) {
                                imageBitmap = transformer.transform(imageBitmap)
                            }
                        }
                        memoryLruCache.putBitmap(memoryKey, imageBitmap)
                        debugLog("onSuccess - from: network")
                        loadingImageMap[diskKey] = false
                        return@async ImageResponse(imageBitmap.toBitmapPainter(), null)
                    }
                } else {
                    var imageBitmap = loadImageBitmap(cacheFile.inputStream())
                    if (diskKey != memoryKey) {
                        for (transformer in request.transformers) {
                            imageBitmap = transformer.transform(imageBitmap)
                        }
                    }
                    memoryLruCache.putBitmap(memoryKey, imageBitmap)
                    debugLog("onSuccess - from: disk")
                    return@async ImageResponse(imageBitmap.toBitmapPainter(), null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                debugLog("onError")
                return@async ImageResponse(null, e)
            }
        }
        return getJob.await()
    }

    fun shutdown() {
        job.cancel()
        client.close()
    }

    fun shutdownAndClearEverything() {
        shutdown()
        clearCache()
    }

    fun clearCache() {
        scope.launch {
            diskLruCache?.clear()
        }
    }

    inner class Request {
        internal var url: String? = null
        internal var file: File? = null

        internal var saveStrategy = SaveStrategy.Original
        internal var transformers = mutableListOf<ITransformation>()

        fun load(url: String): Request {
            this.url = url
            return this
        }

        fun load(file: File): Request {
            this.file = file
            return this
        }

        fun transformations(transformations: List<ITransformation>?): Request {
            if (!transformations.isNullOrEmpty()) {
                transformers.addAll(transformations)
            }
            return this
        }

        fun saveStrategy(strategy: SaveStrategy): Request {
            saveStrategy = strategy
            return this
        }

        suspend fun get(): ImageResponse {
            return try {
                runRequest(this)
            } catch (e: Exception) {
                ImageResponse(null, e)
            }
        }
    }
}