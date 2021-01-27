package com.andretietz.tingeltangel.ui

import com.andretietz.tingeltangel.component
import com.andretietz.tingeltangel.manager.Interactor
import com.andretietz.tingeltangel.manager.ViewState
import com.andretietz.tingeltangel.pencontract.BookInfo
import javafx.beans.property.SimpleListProperty
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import tornadofx.View
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.listview
import tornadofx.runLater
import tornadofx.textfield
import tornadofx.toObservable
import tornadofx.vbox

class ManagerView : View() {

    private val interactor by component().instance<Interactor>()

    init {
        interactor.scope.launch {
            interactor.state.consumeAsFlow().collect { runLater { update(it) } }
        }
    }

    private val bookItems = SimpleListProperty<BookInfo>()

    private fun update(state: ViewState) {
        when (state) {
            is ViewState.Init -> {
                bookItems.value = state.books.toObservable()
            }
        }
    }

    override val root = hbox {
        // left panel
        vbox {
            listview(bookItems) {
                cellFormat {
                    graphic = hbox {
                        // IMAGE
                        setPrefSize(300.0,50.0)
                        imageview(it.image.toString(), lazyload = true) {
                            fitHeight = 50.0
                            fitWidth = 50.0
                        }
                        label(it.title)
                    }
                }
            }
        }

        vbox {

        }
    }
}
