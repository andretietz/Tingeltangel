package com.andretietz.tingeltangel.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest

class ImageCache(
    private val cacheDir: File,
    private val coroutineScope: CoroutineScope
) {
    fun image(url: URL, defaultImage: File, update: (image: File) -> Unit) {
        File(cacheDir, "${md5(url.toString())}${extension(url)}").also { file ->
            if (file.exists()) {
                update(file)
            } else {
                update(defaultImage)
                coroutineScope.launch {
                    url.openStream().use { input ->
                        file.outputStream().use { output -> input.copyTo(output) }
                    }
                    update(file)
                }
            }
        }
    }

    fun clear() {
        coroutineScope.launch {
            cacheDir.deleteRecursively()
        }
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun extension(url: URL): String {
        val uri = url.toString()
        return if (uri.contains(".")) {
            uri.substring(uri.lastIndexOf("."))
        } else {
            ".cache"
        }
    }
}
