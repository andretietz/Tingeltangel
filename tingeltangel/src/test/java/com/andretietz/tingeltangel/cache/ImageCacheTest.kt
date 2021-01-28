package com.andretietz.tingeltangel.cache

import com.andretietz.tingeltangel.manager.ImageCache
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test
import java.io.File

internal class ImageCacheTest {

    companion object {
        private const val SAMPLE_IMAGE = "http://www.bookii-streamingservice.de/files/3/9942/9942_en.png"
        private val CACHE_FOLDER = File("src/test/resources/imagecache/cache")
        private val DUMMY_DEFAULT_FILE = File("dummy")
    }

    init {
        CACHE_FOLDER.mkdirs()
    }

    @Test
    fun `show image cache miss`() = runBlocking {
        val cache = ImageCache(CACHE_FOLDER, this)
        val callback = mockk<(File) -> Unit>()
        val callback2 = mockk<(a: File) -> Unit>()
        val localFile = File(CACHE_FOLDER, "3a0f93222b13a85348b540ce303654b3.png")

        every { callback(any()) } just Runs
        every { callback2(any()) } just Runs

        cache.image(SAMPLE_IMAGE.toHttpUrl().toUrl(), DUMMY_DEFAULT_FILE, callback)
        cache.image(SAMPLE_IMAGE.toHttpUrl().toUrl(), DUMMY_DEFAULT_FILE, callback2)

        val file = slot<File>()

        verify(exactly = 2) { callback(any()) }
        verify(exactly = 1) { callback2(localFile) }

        cache.clear()
    }
}

