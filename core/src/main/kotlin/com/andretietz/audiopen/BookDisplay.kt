package com.andretietz.audiopen

interface BookDisplay {
  /**
   * ID of the book.
   */
  val id: String

  /**
   * title of the book.
   */
  val title: String

  /**
   * Thumbnail of the book.
   */
  val thumbnail: Thumbnail
}
