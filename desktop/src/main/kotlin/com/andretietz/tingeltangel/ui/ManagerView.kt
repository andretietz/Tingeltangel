import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.device.DeviceManager
import com.andretietz.audiopen.remote.BookSource
import com.andretietz.audiopen.view.devices.DeviceListViewModel
import com.andretietz.audiopen.view.sources.RemoteSourceViewModel
import com.andretietz.tingeltangel.ui.DeviceTargetView
import com.andretietz.tingeltangel.ui.RemoteSourceView
import kotlinx.coroutines.CoroutineScope

@Composable
fun ManagerView(
  scope: CoroutineScope,
  bookSources: List<BookSource>,
  deviceManagers: List<DeviceManager>,
  penDetector: AudioPenDetector
) {
  val remoteSourceViewModel = RemoteSourceViewModel(scope, bookSources) {
    // TODO: on copy
    println("Copy Book: $it")
  }
  val deviceListViewModel = DeviceListViewModel(scope, deviceManagers, penDetector) {
    remoteSourceViewModel.deviceConnected(it.isNotEmpty())
  }

  val deviceConnected by remember { mutableStateOf(false) }
  Row {
    Box(Modifier
      .weight(1f)
      .padding(8.dp)
      .fillMaxHeight()
    ) {
      RemoteSourceView(remoteSourceViewModel)
    }
    Box(Modifier
      .weight(1f)
      .padding(8.dp)
      .fillMaxHeight()
    ) {
      DeviceTargetView(deviceListViewModel)
    }
  }
}
