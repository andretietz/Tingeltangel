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
)

/**
 * Name: BOOKii WAS IST WAS Kindergarten Frohe Weihnachten!
Publisher: Tessloff Verlag
Author: Andrea Weller-Essers, Johann Steinstraat
Book Version: 6
URL:
ThumbMD5:
FileMD5:
Book Area Code: en
Type: Buch
ISBN: 978-3-7886-7640-7
Volume: 0

 */
