import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.device.DeviceManager
import com.andretietz.audiopen.remote.BookSource
import com.andretietz.audiopen.view.devices.DeviceListViewModel
import com.andretietz.audiopen.view.sources.RemoteSourceViewModel
import com.andretietz.tingeltangel.cache.ImageCache
import com.andretietz.tingeltangel.ui.DeviceTargetView
import com.andretietz.tingeltangel.ui.RemoteSourceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ManagerView(
  scope: CoroutineScope,
  bookSources: List<BookSource>,
  deviceManagers: List<DeviceManager>,
  penDetector: AudioPenDetector,
  imageCache: ImageCache
) {
  var device: AudioPenDevice? = null
  val remoteSourceViewModel = RemoteSourceViewModel(scope, bookSources) { book ->
    if(device != null) {
      scope.launch { deviceManagers.firstOrNull { it.type == book.type }?.transfer(book, device!!) }
    }
  }
  val deviceListViewModel = DeviceListViewModel(scope, deviceManagers, penDetector) { list, pen ->
    device = pen
    remoteSourceViewModel.deviceConnected(list.isNotEmpty())
  }
  Row {
    Box(Modifier
      .weight(1f)
      .padding(8.dp)
      .fillMaxHeight()
    ) {
      RemoteSourceView(remoteSourceViewModel, imageCache)
    }
    Box(Modifier
      .weight(1f)
      .padding(8.dp)
      .fillMaxHeight()
    ) {
      DeviceTargetView(deviceListViewModel, scope, imageCache)
    }
  }
}
