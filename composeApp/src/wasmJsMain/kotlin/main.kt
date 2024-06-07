import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.dik.torrservermedia.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("TorrServerMedia") {
        App()
    }
}
