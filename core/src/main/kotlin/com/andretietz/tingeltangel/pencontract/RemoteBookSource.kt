package com.andretietz.tingeltangel.pencontract

interface RemoteBookSource {
    /**
     * returns the available books for that source.
     */
    suspend fun availableBooks(): List<BookInfo>

    /**
     * @return the book including all files.
     */
    suspend fun getBook(bookInfo: BookInfo): Book
}
