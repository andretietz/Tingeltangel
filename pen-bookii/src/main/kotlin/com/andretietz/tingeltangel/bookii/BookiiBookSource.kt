package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.pencontract.Book
import com.andretietz.tingeltangel.pencontract.BookInfo
import com.andretietz.tingeltangel.pencontract.BookSource
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.File


internal class BookiiBookSource(
    private val api: BookiiApi,
    private val cacheDir: File
) : BookSource {


    override suspend fun availableBooks() = api.versions().map { it.key }
        .chunked(10)
        .take(1)// TODO tmp
        .map { api.info(it.joinToString(",") { item -> "\"$item\"" }) }
        .flatten()
        .map { info ->
            BookInfo(
                id = info.id.toString(),
                type = BookiiContract.TYPE,
                title = info.title,
                version = info.version,
                image = MEDIA_URL.format(info.publisher.id, info.id, info.id, info.areaCode).toHttpUrlOrNull()?.toUrl()
            )
        }
        .toSet()

    override suspend fun getBook(book: BookInfo): Book {
        val size = api.fileSize(book.id, book.version)
        println(size)
        return Book(book)
    }

    companion object {
        private const val MEDIA_URL = "http://www.bookii-streamingservice.de/files/%d/%d/%d_%s.png"
    }
}
