package com.andretietz.tingeltangel.pencontract

import java.net.URL

/**
 * Contains all information of a book, but not the actual files, except for the image.
 */
data class BookInfo(
    /**
     * Id of the book.
     */
    val id: String,
    /**
     * Type of the pen (ting, bookii, etc.)
     */
    val type: String,
    /**
     * Title of the book.
     */
    val title: String,

    val version: Int,
    /**
     * Image of the book.
     */
    val image: URL?
)
