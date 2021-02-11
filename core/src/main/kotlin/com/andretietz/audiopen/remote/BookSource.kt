package com.andretietz.audiopen.remote

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type

/**
 * Every audiobook producer has it's own source of online books. This interface abstracts the differences.
 */
interface BookSource {
  /**
   * Type of the book source.
   */
  val type: Type

  /**
   * @return All available books for that source.
   */
  suspend fun availableBooks(): List<BookDisplay>
}
