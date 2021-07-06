package com.andretietz.tingeltangel.cache

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
internal class ImageCacheTest {

  companion object {
    private const val SAMPLE_IMAGE = "http://www.bookii-streamingservice.de/files/3/9942/9942_en.png"
    private val CACHE_FOLDER = File("src/test/resources/imagecache/cache")
  }

  @get:Rule
  val coroutineRule = TestCoroutineRule()

  init {
    CACHE_FOLDER.mkdirs()
  }

//  @Test
//  fun `show image cache miss`() = runBlockingTest {
//    val cache = ImageCache(CACHE_FOLDER)
//    val localFile = File(CACHE_FOLDER, "3a0f93222b13a85348b540ce303654b3.png")
//    val url = SAMPLE_IMAGE.toHttpUrl().toUrl()
//
//    try {
//      assertThat(localFile).doesNotExist()
//      val image1 = cache.image(url)
//      assertThat(localFile).exists().isFile.isEqualTo(image1)
//    } finally {
//      cache.clear()
//    }
//  }
}
