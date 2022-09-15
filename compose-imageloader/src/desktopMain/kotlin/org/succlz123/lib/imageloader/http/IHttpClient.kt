package org.succlz123.lib.imageloader.http

import kotlinx.coroutines.CoroutineDispatcher

interface IHttpClient {

    fun dispatcher(): CoroutineDispatcher

    suspend fun pullImage(url: String, key: String): ResponseData?

    fun close()
}