import androidx.compose.ui.window.ComposeUIViewController
import com.dik.torrservermedia.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
