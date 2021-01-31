package com.andretietz.tingeltangel.ui

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type
import com.andretietz.audiopen.view.devices.DeviceListInteractor
import com.andretietz.audiopen.view.devices.DeviceListViewState
import com.andretietz.audiopen.view.sources.RemoteSourceInteractor
import com.andretietz.audiopen.view.sources.RemoteSourceViewState
import com.andretietz.tingeltangel.cache.ImageCache
import com.andretietz.tingeltangel.component
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Region
import javafx.scene.text.Font
import kotlinx.coroutines.CoroutineScope
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

  private val coroutineScope by component().instance<CoroutineScope>()
  private val remoteInteractor by component().instance<RemoteSourceInteractor>()
  private val deviceListInteractor by component().instance<DeviceListInteractor>()
  private val imageCache by component().instance<ImageCache>()

  private val localBooks = SimpleListProperty<BookDisplay>()
  private val localBookFilter = SimpleStringProperty().onChange {
    remoteInteractor.filterRemoteBooks(it)
  }
  private val deviceBooks = SimpleListProperty<BookDisplay>()
  private val deviceBookFilter = SimpleStringProperty().onChange {
    deviceListInteractor.filterDeviceBooks(it)
  }

  private val bookTypes = SimpleListProperty<Type>()
  private val currentlySelectedBookType = SimpleObjectProperty<Type>()

  private val audioPenTypes = SimpleListProperty<AudioPenDevice>()
  private val currentlySelectedAudioPen = SimpleObjectProperty<AudioPenDevice>()

  private var selectedLocalBookInfo: BookDisplay? = null

  init {
    title = messages["view_title"]

    coroutineScope.launch {
      remoteInteractor.state.consumeAsFlow()
        .collect { runLater { updateRemoteSource(it) } }
    }
    coroutineScope.launch {
      deviceListInteractor.state.consumeAsFlow()
        .collect { runLater { updateDeviceList(it) } }
    }

    currentlySelectedAudioPen.onChange {
      deviceListInteractor.selectAudioPen(it)
    }

    currentlySelectedBookType.onChange {
      remoteInteractor.selectBookSource(it)
    }
  }

  private fun updateDeviceList(state: DeviceListViewState) {
    when (state) {
      is DeviceListViewState.DeviceListUpdate -> {
        audioPenTypes.value = state.devices.toObservable()
        currentlySelectedAudioPen.value
        currentlySelectedAudioPen.value = state.devices.firstOrNull()
      }
      is DeviceListViewState.DeviceBookUpdate -> deviceBooks.value = state.books.toObservable()
    }
  }

  private fun updateRemoteSource(state: RemoteSourceViewState) {
    when (state) {
      is RemoteSourceViewState.Init -> {
        currentlySelectedBookType.value = state.bookTypes.first()
        bookTypes.value = state.bookTypes.toObservable()
      }
      is RemoteSourceViewState.BookListUpdate -> localBooks.value = state.bookInfos.toObservable()
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
            // remoteInteractor.transferBookToDevice(selectedLocalBookInfo, currentlySelectedAudioPen.value)
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
    deviceBooks: SimpleListProperty<BookDisplay>,
    deletable: Boolean,
    onSelect: (BookDisplay) -> Unit = {}
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
              imageview(it.thumbnail.toString()) {
                fitHeight = IMAGE_MAX_HEIGHT
                fitWidth = IMAGE_MAX_HEIGHT
                imageCache.image(it.thumbnail) { file ->
                  val img = Image(file.inputStream())
                  val (width, height) = scaleDownAndKeepRatio(img)
                  setPrefSize(width, height)
                  runLater { image = img }
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
                  action {
                    // interactor.removeFromDevice(it)
                  }
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
