package com.andretietz.tingeltangel.pencontract

interface BookSource {
    /**
     * returns the available books for that source.
     */
    suspend fun availableBooks(): Set<BookInfo>

    /**
     * @return the book including all files.
     */
    suspend fun getBook(book: BookInfo): Book
}
