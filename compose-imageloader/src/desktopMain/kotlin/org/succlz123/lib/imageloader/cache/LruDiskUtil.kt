package org.succlz123.lib.imageloader.cache

import java.io.*
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.jvm.JvmField
import kotlin.text.toByteArray

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * Junk drawer of utility methods.
 */
object LruDiskUtil {
    @JvmField
    val US_ASCII = Charset.forName("US-ASCII")

    @JvmField
    val UTF_8 = Charset.forName("UTF-8")

    @JvmStatic
    @Throws(IOException::class)
    fun readFully(reader: Reader): String {
        return try {
            val writer = StringWriter()
            val buffer = CharArray(1024)
            var count: Int
            while (reader.read(buffer).also { count = it } != -1) {
                writer.write(buffer, 0, count)
            }
            writer.toString()
        } finally {
            reader.close()
        }
    }

    /**
     * Deletes the contents of `dir`. Throws an IOException if any file
     * could not be deleted, or if `dir` is not a readable directory.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun deleteContents(dir: File) {
        val files = dir.listFiles() ?: throw IOException("not a readable directory: $dir")
        for (file in files) {
            if (file.isDirectory) {
                deleteContents(file)
            }
            if (!file.delete()) {
                throw IOException("failed to delete file: $file")
            }
        }
    }

    @JvmStatic
    fun closeQuietly( /*Auto*/closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }
        }
    }

    fun hashKey(key: String): String {
        val cacheKey: String = try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray())
            bytesToHexString(mDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            key.hashCode().toString()
        }
        return cacheKey
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }
}