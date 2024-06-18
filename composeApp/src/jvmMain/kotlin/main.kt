import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import java.awt.Dimension
import com.dik.torrservermedia.RootUi
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.theme.AppTheme
import com.dik.torrservermedia.utils.runOnUiThread
import kotlinx.coroutines.Dispatchers
import org.koin.mp.KoinPlatform
import kotlinx.coroutines.runBlocking


fun main() {
    val lifecycle = LifecycleRegistry()
    val root = runOnUiThread {
        KoinModules.init()
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            featureTorrentList = KoinPlatform.getKoin().get()
        )
    }

    application {
        Window(
            title = "TorrServerMedia",
            state = rememberWindowState(width = 1000.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 600)
//            App()
            Surface(modifier = Modifier.fillMaxSize()) {
                AppTheme {
                    RootUi(root)
                }
            }
        }
    }
}