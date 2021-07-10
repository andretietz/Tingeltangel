import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail
import com.andretietz.tingeltangel.cache.ImageCache
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun BookItemView(
  item: BookDisplay,
  imageCache: ImageCache,
  iconView: @Composable (scope: RowScope) -> Unit = {}
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(4.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
      ImageView(item.thumbnail, item.title, imageCache)
      Text(
        "${item.title} (${item.id})", modifier = Modifier
          .weight(1f)
          .align(Alignment.CenterVertically)
      )
      iconView(this)
    }
  }
}

@Composable
fun ImageView(
  thumbnail: Thumbnail,
  contentDescription: String,
  imageCache: ImageCache,
  scope: CoroutineScope = CoroutineScope(CoroutineName("ImageScope"))
) {
  val modifier = Modifier
    .padding(8.dp)
    .width(80.dp)
    .height(80.dp)
  var imageState by remember { mutableStateOf<ImageBitmap?>(null) }
  when (thumbnail) {
    is Thumbnail.Remote -> {
      val image = imageState
      image?.let { Image(it, contentDescription, modifier) }
      scope.launch {
        val file = imageCache.image(thumbnail.url)
        val bitmap: ImageBitmap? = fileToImageBitmap(file)
        if (bitmap != null) {
          withContext(Dispatchers.Main) { imageState = bitmap }
        }
      }
    }
    is Thumbnail.Local -> {
      val image = imageState
      image?.let { Image(it, contentDescription, modifier) }
      scope.launch {
        val bitmap: ImageBitmap? = fileToImageBitmap(thumbnail.file)
        if (bitmap != null) {
          withContext(Dispatchers.Main) { imageState = bitmap }
        }
      }
    }
  }
}


suspend fun fileToImageBitmap(file: File): ImageBitmap? = withContext(Dispatchers.Default) {
  try {
    org.jetbrains.skija.Image.makeFromEncoded(file.readBytes()).asImageBitmap()
  } catch (_: Throwable) {
    null
  }
}
