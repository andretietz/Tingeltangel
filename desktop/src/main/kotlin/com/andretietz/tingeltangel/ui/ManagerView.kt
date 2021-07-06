import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andretietz.tingeltangel.ui.DeviceTargetView
import com.andretietz.tingeltangel.ui.RemoteSourceView

@Composable
fun ManagerView() {


  Row {
    Box(Modifier.weight(1f)) {
      RemoteSourceView()
    }
    Box(Modifier.weight(1f)) {
      DeviceTargetView()
    }
  }
}
