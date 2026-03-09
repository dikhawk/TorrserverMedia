package com.dik.torrservermedia
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.nanigation.ChildConfig
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.utils.runOnUiThread
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension


fun main(args: Array<String>) {
    val lifecycle = LifecycleRegistry()
    KoinModules.init()
    val root = runOnUiThread {
        DefaultRootComponent(
            initialConfiguration = ChildConfig.TorrentList,
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    root.startServer()

    application {
        Window(
            title = stringResource(Res.string.app_name),
            state = rememberWindowState(width = 1000.dp, height = 600.dp),
            onCloseRequest = {
                root.stopServer()
                exitApplication()
            }
        ) {
            window.minimumSize = Dimension(400, 200)

            Surface(modifier = Modifier.fillMaxSize()) {
                RootUi(root)
            }
        }
    }
}