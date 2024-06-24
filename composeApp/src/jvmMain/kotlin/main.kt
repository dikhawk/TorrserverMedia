import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.torrserverapi.cmd.KmpCmdRunner
import com.dik.torrserverapi.cmd.KmpServerCommands
import java.awt.Dimension
import com.dik.torrservermedia.RootUi
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.theme.AppTheme
import com.dik.torrservermedia.utils.runOnUiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin


fun main() {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val lifecycle = LifecycleRegistry()
    KoinModules.init()
    val root = runOnUiThread {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            featureTorrentList = inject()
        )
    }
    val rootDirectory = System.getProperty("user.dir")

    scope.launch(Dispatchers.Default) {
        KmpServerCommands.startServer("TorrServer-linux-amd64", "/home/dik/TorrServer")
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
                        Button(onClick = { KmpCmdRunner.stopRunnedProcesses() }) {
                            Text("Stop torrserver")
                        }
                        Text(rootDirectory)
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