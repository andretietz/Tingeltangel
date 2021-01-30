package com.andretietz.tingeltangel.ui

import com.andretietz.tingeltangel.component
import com.andretietz.tingeltangel.manager.Interactor
import com.andretietz.tingeltangel.manager.ViewState
import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.BookInfo
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Region
import javafx.scene.text.Font
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.combobox
import tornadofx.form
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.listview
import tornadofx.onChange
import tornadofx.onUserSelect
import tornadofx.runLater
import tornadofx.textfield
import tornadofx.toObservable
import tornadofx.vbox

class ManagerView : View() {

  private val interactor by component().instance<Interactor>()

  private val localBooks = SimpleListProperty<BookInfo>()
  private val localBookFilter = SimpleStringProperty().onChange {
    interactor.filterLocalBooks(it)
  }
  private val deviceBooks = SimpleListProperty<BookInfo>()
  private val deviceBookFilter = SimpleStringProperty().onChange {
    interactor.filterDeviceBooks(it)
  }

  private val bookTypes = SimpleListProperty<AudioPenContract.Type>()
  private val currentlySelectedBookType = SimpleObjectProperty<AudioPenContract.Type>()

  private val audioPenTypes = SimpleListProperty<AudioPenDevice>()
  private val currentlySelectedAudioPen = SimpleObjectProperty<AudioPenDevice>()

  private var selectedLocalBookInfo: BookInfo? = null

  init {
    title = messages["view_title"]
    interactor.scope.launch {
      interactor.state.consumeAsFlow().collect { runLater { update(it) } }
    }

    currentlySelectedAudioPen.onChange {
      interactor.selectAudioPen(it)
    }

    currentlySelectedBookType.onChange {
      interactor.selectBookSource(it)
    }
  }

  private fun update(state: ViewState) {
    when (state) {
      is ViewState.Init -> {
        currentlySelectedBookType.value = state.bookTypes.first()
        bookTypes.value = state.bookTypes.toObservable()
      }
      is ViewState.LocalBookListUpdate -> localBooks.value = state.bookInfos.toObservable()
      is ViewState.DeviceListUpdate -> {
        audioPenTypes.value = state.devices.toObservable()
        currentlySelectedAudioPen.value
        currentlySelectedAudioPen.value = state.devices.firstOrNull()
      }
      is ViewState.DeviceBookUpdate -> deviceBooks.value = state.books.toObservable()
    }
  }

  override val root = form {
    hbox {
      vbox {
        combobox(currentlySelectedBookType, bookTypes) {
          cellFormat { text = messages["official_book_source"].format(it.name) }
        }
        textfield(localBookFilter) {
          promptText = messages["textfield_search_official_label"]
        }
        provideList(this, localBooks, false) {
          selectedLocalBookInfo = it
        }
      }
      vbox {
        alignment = Pos.CENTER
        button("->") {
          action {
            interactor.transferBookToDevice(selectedLocalBookInfo, currentlySelectedAudioPen.value)
          }
        }
      }
      vbox {
        combobox(currentlySelectedAudioPen, audioPenTypes) {
          cellFormat {
            text = "${it.type.name} (${it.rootDirectory.path})"
          }
        }
        textfield(deviceBookFilter) {
          promptText = messages["textfield_search_device_label"]
        }
        provideList(this, deviceBooks, true)
      }
    }
  }

  @SuppressWarnings("Detekt.UnnecessaryApply")
  private fun provideList(
    region: Region,
    deviceBooks: SimpleListProperty<BookInfo>,
    deletable: Boolean,
    onSelect: (BookInfo) -> Unit = {}
  ) {
    region.apply {
      listview(deviceBooks) {
        prefWidth = LIST_WIDTH
        onUserSelect(1) { onSelect(it) }
        cellFormat {
          graphic = hbox {
            setPrefSize(LIST_WIDTH, IMAGE_MAX_HEIGHT)
            vbox {
              setPrefSize(IMAGE_MAX_HEIGHT, IMAGE_MAX_HEIGHT)
              alignment = Pos.CENTER
              imageview(it.image.toString()) {
                fitHeight = IMAGE_MAX_HEIGHT
                fitWidth = IMAGE_MAX_HEIGHT
                it.image?.let {
                  interactor.loadImage(it) { file ->
                    val img = Image(file.inputStream())
                    val (width, height) = scaleDownAndKeepRatio(img)
                    setPrefSize(width, height)
                    runLater { image = img }
                  }
                }
              }
            }
            vbox { prefWidth = IMAGE_TEXT_SPACING }
            vbox {
              alignment = Pos.CENTER_LEFT
              prefWidth = LIST_WIDTH - IMAGE_MAX_HEIGHT - IMAGE_MAX_HEIGHT - if (deletable) IMAGE_MAX_HEIGHT else 0.0
              label("${it.title.trim()} (${it.id})") {
                font = Font.font(LIST_ITEM_FONT_SIZE)
              }
            }
            if (deletable) {
              vbox {
                alignment = Pos.CENTER
                prefWidth = IMAGE_MAX_HEIGHT
                button(messages["button_delete_book"]) {
                  action { interactor.removeFromDevice(it) }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun scaleDownAndKeepRatio(image: Image) = when {
    image.height == image.width -> arrayOf(
      IMAGE_MAX_HEIGHT,
      IMAGE_MAX_HEIGHT
    )
    image.height > image.width -> arrayOf(
      IMAGE_MAX_HEIGHT * image.width / image.height,
      IMAGE_MAX_HEIGHT
    )
    else -> arrayOf(
      IMAGE_MAX_HEIGHT,
      IMAGE_MAX_HEIGHT * image.height / image.width
    )
  }

  companion object {
    private const val IMAGE_MAX_HEIGHT = 70.0
    private const val LIST_WIDTH = 700.0
    private const val IMAGE_TEXT_SPACING = 10.0
    private const val LIST_ITEM_FONT_SIZE = 15.0
  }
}
