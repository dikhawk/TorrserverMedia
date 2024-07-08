import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.common.utils.platformName
import com.dik.common.cmd.KmpCmdRunner
import java.awt.Dimension
import com.dik.torrservermedia.RootUi
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.theme.AppTheme
import com.dik.torrservermedia.utils.runOnUiThread


fun main() {
    val lifecycle = LifecycleRegistry()
    KoinModules.init()
    val root = runOnUiThread {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            featureTorrentList = inject()
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
                    Column {
                        RootUi(root)
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                KmpCmdRunner.stopRunnedProcesses()
            }
        }
    }
}