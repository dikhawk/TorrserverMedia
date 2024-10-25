package com.dik.torrservermedia

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.dik.common.utils.successResult
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.uikit.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        KoinModules.init {
            androidContext(INSTANCE)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val torrserverApi: TorrserverApi = inject()
        val commands: TorrserverCommands = torrserverApi.torrserverCommands()
        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            featureTorrentListApi = inject(),
            featureSettingsApi = inject()
        )

        lifecycleScope.launch {
            val isInstalledServer = commands.isServerInstalled().successResult() ?: false
            if (isInstalledServer) commands.startServer()
        }

        enableEdgeToEdge()
        setContent {
            val permissions = remember {
                mutableListOf<String>().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            val notificationPermission = rememberMultiplePermissionsState(permissions)

            AppTheme {
                RootUi(component = root)
            }

            LaunchedEffect(Unit) {
                if (!notificationPermission.allPermissionsGranted)
                    notificationPermission.launchMultiplePermissionRequest()
            }
        }
    }
}