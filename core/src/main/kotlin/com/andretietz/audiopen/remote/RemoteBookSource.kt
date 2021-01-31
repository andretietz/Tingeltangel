package com.andretietz.audiopen.remote

import com.andretietz.audiopen.Book
import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.Type

/**
 * Every audiobook producer has it's own source of online books. This interface abstracts the differences.
 */
interface RemoteBookSource {
  /**
   * Type of the book source.
   */
  val type: Type

  /**
   * @return All available books for that source.
   */
  suspend fun availableBooks(): List<BookInfo>

  /**
   * @return the book including all files.
   */
  suspend fun getBook(bookInfo: BookInfo): Book
}
