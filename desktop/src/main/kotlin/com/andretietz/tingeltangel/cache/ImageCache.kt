package com.andretietz.tingeltangel.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.security.MessageDigest

class ImageCache(
  private val cacheDir: File,
  private val coroutineScope: CoroutineScope
) {

  init {
    if (!cacheDir.exists()) {
      cacheDir.mkdirs()
    }
  }

  fun image(url: URI, update: (image: File) -> Unit) {
    if (url.scheme == URL_FILE_PROTOCOL) {
      update(File(url))
    } else {
      File(cacheDir, "${md5(url.toString())}${extension(url.toURL())}").also { file ->
        if (file.exists()) {
          update(file)
        } else {
          if (file.createNewFile()) {
            coroutineScope.launch {
              url.toURL().openStream().use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
              }
              update(file)
            }
          }
        }
      }
    }
  }

  fun clear() {
    coroutineScope.launch {
      cacheDir.deleteRecursively()
    }
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

  companion object {
    private const val URL_FILE_PROTOCOL = "file"
  }
}
