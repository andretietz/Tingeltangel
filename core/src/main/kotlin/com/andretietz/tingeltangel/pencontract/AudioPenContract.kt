package com.andretietz.tingeltangel.pencontract

interface AudioPenContract {
    fun source() : BookSource
    fun hardware() : HardwareContract
}
