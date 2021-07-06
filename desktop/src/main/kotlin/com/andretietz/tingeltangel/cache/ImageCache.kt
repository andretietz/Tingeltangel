package com.andretietz.tingeltangel.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.IllegalStateException
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest

class ImageCache(
  private val cacheDir: File
) {

  init {
    if (!cacheDir.exists()) {
      cacheDir.mkdirs()
    }
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun image(url: URL): File = withContext(Dispatchers.IO) {
    File(cacheDir, "${md5(url.toString())}${extension(url)}").also { file ->
      if (file.exists()) {
        return@withContext file
      } else {
        if (file.createNewFile()) {
          url.openStream().use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
          }
          return@withContext file
        }
      }
    }
    throw IllegalStateException()
  }

  suspend fun clear() = withContext(Dispatchers.Default) {
    cacheDir.deleteRecursively()
  }

  @SuppressWarnings("Detekt.MagicNumber")
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
