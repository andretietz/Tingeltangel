package com.andretietz.tingeltangel.pencontract

interface AudioPenContract {

    /**
     * Type of the book source.
     */
    val type: String
    fun source() : BookSource
    fun hardware() : HardwareContract
}
