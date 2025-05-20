package com.dik.torrservermedia
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.setLocalization
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.ChildConfig
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.utils.runOnUiThread
import com.dik.uikit.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import torrservermedia.composeapp.generated.resources.Res
import torrservermedia.composeapp.generated.resources.app_name
import java.awt.Dimension


fun main(args: Array<String>) {
    val lifecycle = LifecycleRegistry()
    KoinModules.init()

    val pathToFile = args.firstOrNull()
    val torrServerApi: TorrserverApi = inject()
    val dispatchers: AppDispatchers = inject()
    val appSettings: AppSettings = inject()
    val commands = torrServerApi.torrserverCommands()
    val scope = CoroutineScope(dispatchers.defaultDispatcher() + SupervisorJob())
    val root = runOnUiThread {
        DefaultRootComponent(
            initialConfiguration = ChildConfig.TorrentList,
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            featureTorrentListApi = inject(),
            featureSettingsApi = inject(),
        )
    }

    scope.launch {
        commands.startServer()
        setLocalization(appSettings.language)
    }

    application {
        Window(
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 1000.dp, height = 600.dp),
            onCloseRequest = {
                runBlocking { commands.stopServer() }
                exitApplication()
            }
        ) {
            window.minimumSize = Dimension(400, 200)
            Surface(modifier = Modifier.fillMaxSize()) {
                AppTheme {
                    RootUi(root)
                }
            }
        }
    }
}