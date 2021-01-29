package com.andretietz.tingeltangel.ui

import com.andretietz.tingeltangel.component
import com.andretietz.tingeltangel.manager.Interactor
import com.andretietz.tingeltangel.manager.ViewState
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.BookInfo
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.text.Font
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import tornadofx.*

class ManagerView : View() {

  private val interactor by component().instance<Interactor>()

  init {
    title = messages["view_title"]
    interactor.scope.launch {
      interactor.state.consumeAsFlow().collect { runLater { update(it) } }
    }
  }

  private val localBooks = SimpleListProperty<BookInfo>()
  private val localBookFilter = SimpleStringProperty().onChange {
    interactor.filterLocalBooks(it)
  }
  private val deviceBooks = SimpleListProperty<BookInfo>()
  private val deviceBookFilter = SimpleStringProperty().onChange {
    interactor.filterDeviceBooks(it)
  }

  private val bookTypes = SimpleListProperty<String>()
  private val currentlySelectedBookType = SimpleObjectProperty<String>()
//  private val bookTypes = SimpleListProperty<PenType>()
//  private val currentlySelectedBookType = SimpleObjectProperty<PenType>()

  private val audioPenTypes = SimpleListProperty<AudioPenDevice>()
  private val currentlySelectedAudioPen = SimpleObjectProperty<AudioPenDevice>()

  private fun update(state: ViewState) {
    when (state) {
      is ViewState.Init -> {
        localBooks.value = state.books.toObservable()
        currentlySelectedBookType.value = state.bookTypes.first().name
        bookTypes.value = state.bookTypes.map { it.name }.toObservable()
      }
      is ViewState.LocalBookListUpdate -> {
        localBooks.value = state.books.toObservable()
      }
      is ViewState.DeviceListUpdate -> {
        audioPenTypes.value = state.devices.toObservable()
        currentlySelectedAudioPen.value = state.devices.firstOrNull()
        // TODO books:?
      }
    }
  }

  override val root = form {
    hbox {
      vbox {
        combobox(currentlySelectedBookType, bookTypes)
        textfield(localBookFilter) {
          // TODO: localize
          promptText = "Search"
        }
        listview(localBooks) {
          prefWidth = 700.0
          cellFormat {
            graphic = hbox {
              setPrefSize(700.0, IMAGE_MAX_HEIGHT)
              vbox {
                setPrefSize(IMAGE_MAX_HEIGHT, IMAGE_MAX_HEIGHT)
                alignment = Pos.CENTER
                imageview(it.image.toString()) {
                  fitHeight = IMAGE_MAX_HEIGHT
                  fitWidth = IMAGE_MAX_HEIGHT
                  it.image?.let {
                    interactor.loadImage(it) { file ->
                      val img = Image(file.inputStream())
                      when {
                        img.height == img.width -> {
                          fitHeight = IMAGE_MAX_HEIGHT
                          fitWidth = IMAGE_MAX_HEIGHT
                        }
                        img.height > img.width -> {
                          fitHeight = IMAGE_MAX_HEIGHT
                          fitWidth = IMAGE_MAX_HEIGHT * img.width / img.height
                        }
                        else -> {
                          fitWidth = IMAGE_MAX_HEIGHT
                          fitHeight = IMAGE_MAX_HEIGHT * img.height / img.width
                        }
                      }
                      runLater { image = img }
                    }
                  }

                }
              }
              vbox { prefWidth = 10.0 }
              vbox {
                alignment = Pos.CENTER
                label("${it.title.trim()} (${it.id})") {
                  font = Font.font(15.0)
                }
              }

            }
          }
        }
      }
      vbox {
        alignment = Pos.CENTER
        button("->") {

        }
      }
      vbox {
        combobox(currentlySelectedAudioPen) {
          items = audioPenTypes
          cellFormat {
            text = it.type
          }
        }
        textfield(deviceBookFilter) {
          // TODO: localize
          promptText = "Search"
        }
        listview(deviceBooks) {
          prefWidth = 700.0
          cellFormat {
            graphic = hbox {
              setPrefSize(700.0, IMAGE_MAX_HEIGHT)
              vbox {
                setPrefSize(IMAGE_MAX_HEIGHT, IMAGE_MAX_HEIGHT)
                alignment = Pos.CENTER
                imageview(it.image.toString()) {
                  fitHeight = IMAGE_MAX_HEIGHT
                  fitWidth = IMAGE_MAX_HEIGHT
                  it.image?.let {
                    interactor.loadImage(it) { file ->
                      val img = Image(file.inputStream())
                      when {
                        img.height == img.width -> {
                          fitHeight = IMAGE_MAX_HEIGHT
                          fitWidth = IMAGE_MAX_HEIGHT
                        }
                        img.height > img.width -> {
                          fitHeight = IMAGE_MAX_HEIGHT
                          fitWidth = IMAGE_MAX_HEIGHT * img.width / img.height
                        }
                        else -> {
                          fitWidth = IMAGE_MAX_HEIGHT
                          fitHeight = IMAGE_MAX_HEIGHT * img.height / img.width
                        }
                      }
                      runLater { image = img }
                    }
                  }

                }
              }
              vbox { prefWidth = 10.0 }
              vbox {
                alignment = Pos.CENTER
                label("${it.title.trim()} (${it.id})") {
                  font = Font.font(15.0)
                }
              }

            }
          }
        }
      }
    }
  }

  companion object {
    const val IMAGE_MAX_HEIGHT = 70.0
  }
}
