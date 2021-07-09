import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.unit.dp
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail
import com.andretietz.tingeltangel.cache.ImageCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun BookItemView(
  item: BookDisplay,
  imageCache: ImageCache,
  scope: CoroutineScope,
  iconView: @Composable (scope: RowScope) -> Unit
) {
  var imageState by remember { mutableStateOf<ImageBitmap?>(null) }
  Card(modifier = Modifier
    .fillMaxWidth()
    .padding(4.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
      val modifier = Modifier
        .padding(8.dp)
        .width(80.dp)
        .height(80.dp)
      when (val thumbnail = item.thumbnail) {
        is Thumbnail.Remote -> {
          val image = imageState
          if (image != null) {

            Image(image, item.title, modifier)
          }
          scope.launch {
            val file = imageCache.image(thumbnail.url)
            val bitmap = withContext(Dispatchers.Default) {
              org.jetbrains.skija.Image.makeFromEncoded(file.readBytes()).asImageBitmap()
            }
            withContext(Dispatchers.Main) {
              imageState = bitmap
            }
          }
        }
        is Thumbnail.Local -> {
          Image(imageFromResource(thumbnail.file.absolutePath), "",
            modifier = modifier)
        }
      }
      Text("${item.title} (${item.id})", modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically))
      iconView(this)
    }
  }
}
