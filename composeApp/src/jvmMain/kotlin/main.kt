import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrservermedia.RootUi
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.utils.runOnUiThread
import com.dik.uikit.theme.AppTheme
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import torrservermedia.composeapp.generated.resources.Res
import torrservermedia.composeapp.generated.resources.app_name
import java.awt.Dimension


fun main() {
    val lifecycle = LifecycleRegistry()
    KoinModules.init()
    val torrServerApi: TorrserverApi = inject()
    val root = runOnUiThread {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            featureTorrentListApi = inject(),
            featureSettingsApi = inject(),
        )
    }

    application {
        Window(
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 1000.dp, height = 600.dp),
            onCloseRequest = {
                runBlocking { torrServerApi.torrserverCommands().stopServer() }
                exitApplication()
            }
        ) {
            window.minimumSize = Dimension(800, 600)
            Surface(modifier = Modifier.fillMaxSize()) {
                AppTheme {
                    Column {
                        RootUi(root)
                    }
                }
            }
        }
    }
}