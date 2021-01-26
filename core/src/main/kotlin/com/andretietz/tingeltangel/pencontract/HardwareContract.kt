package com.andretietz.tingeltangel.pencontract

import kotlinx.coroutines.flow.Flow

interface HardwareContract {
    fun update(): Flow<String>
}
