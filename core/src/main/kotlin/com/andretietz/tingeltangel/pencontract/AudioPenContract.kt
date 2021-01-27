package com.andretietz.tingeltangel.pencontract

import java.io.File

interface AudioPenContract {

    /**
     * Type of the book source.
     */
    val type: String
    fun source(): BookSource

    suspend fun verifyDevice(rootFolder: File): AudioPenDevice?
}
