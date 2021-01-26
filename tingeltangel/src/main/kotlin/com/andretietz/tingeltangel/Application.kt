package com.andretietz.tingeltangel

import com.andretietz.tingeltangel.bookii.BookiiBookSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tornadofx.launch

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application().run()
        }

        val coroutineScope = CoroutineScope(Dispatchers.IO)
    }

    fun run() {
//        launch<MainApp>()
        val source = BookiiBookSource()
        runBlocking {
            async(coroutineContext) {
                source.availableBooks()
                    .forEach {
                        println(it.toString())
                    }

            }
        }

    }
}
