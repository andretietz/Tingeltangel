package com.andretietz.audiopen

import java.net.URL

/**
 * A book as it can be received by the api of the according audiopen.
 */
data class BookInfo(
  /**
   * Id of the book.
   */
  override val id: String,
  /**
   * Type of the pen (ting, bookii, etc.)
   */
  val type: Type,
  /**
   * Title of the book.
   */
  override val title: String,

  val areaCode: String,

  val version: Int,
  /**
   * Image of the book.
   */
  val image: URL?,

  /**
   * Url of the content-file.
   */
  val contentUrl: URL,

  // ugly
  val publisherName: String,
  val authorName: String,
  val mediaType: String,
  val volume: Int,
  val isbn: String?
) : BookDisplay {
  override val thumbnail = image?.toURI()!!
}
