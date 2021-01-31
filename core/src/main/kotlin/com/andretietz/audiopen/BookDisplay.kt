package com.andretietz.audiopen

import java.io.File

interface BookDisplay {
  /**
   * ID of the book.
   */
  val id: String

  /**
   * title of the book.
   */
  val name: String

  /**
   * Thumbnail of the book.
   */
  val thumbnail: File
}
