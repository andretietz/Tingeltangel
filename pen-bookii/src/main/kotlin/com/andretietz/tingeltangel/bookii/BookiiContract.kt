package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.BookSource
import com.andretietz.tingeltangel.pencontract.HardwareContract

class BookiiContract : AudioPenContract {
    override fun source(): BookSource {
        TODO("Not yet implemented")
    }

    override fun hardware(): HardwareContract {
        TODO("Not yet implemented")
    }
}
