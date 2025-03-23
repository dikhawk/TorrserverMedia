package com.dik.torrservermedia

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.CurrentActivityProvider
import com.dik.common.i18n.setLocalization
import com.dik.torrserverapi.model.TorrserverServiceManager
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.ChildConfig
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.utils.pathToFile
import com.dik.uikit.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import java.lang.ref.WeakReference

class AndroidApp : Application(), CurrentActivityProvider {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    private var currentActivity: WeakReference<Activity>? = null

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActivity = WeakReference(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                currentActivity = WeakReference(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = WeakReference(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                currentActivity = WeakReference(activity)
            }

            override fun onActivityStopped(activity: Activity) {
                currentActivity = WeakReference(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                currentActivity = WeakReference(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                currentActivity = WeakReference(activity)
            }

        })

        KoinModules.init {
            androidContext(INSTANCE)
        }
    }

    override fun getActiveActivity(): Activity? {
        return currentActivity?.get()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
class AppActivity : ComponentActivity() {

    private val torrserverService: TorrserverServiceManager = inject()
    private val appSettings: AppSettings = inject()
    private lateinit var rootComponent: DefaultRootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            torrserverService.startService()
            setLocalization(appSettings.language)
        }

        rootComponent = DefaultRootComponent(
            initialConfiguration = initialConfiguration(intent) ?: ChildConfig.TorrentList,
            componentContext = defaultComponentContext(),
            featureTorrentListApi = inject(),
            featureSettingsApi = inject(),
        )

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
                RootUi(component = rootComponent)
            }

            LaunchedEffect(Unit) {
                notificationPermission.launchMultiplePermissionRequest()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val config = initialConfiguration(intent) ?: return

        rootComponent.onOpenContent(config)
    }

    private fun initialConfiguration(intent: Intent): ChildConfig? {
        val action = intent.action
        val data: Uri? = intent.data
        val type = intent.type
        val scheme = intent.scheme
        val torrentType = "application/x-bittorrent"
        val magnetScheme = "magnet"

        if (action != Intent.ACTION_VIEW || data == null) return null

        return when {
            type == torrentType -> ChildConfig.OpenTorrent(data.pathToFile(this) ?: "")
            scheme == magnetScheme -> ChildConfig.OpenMagnet(data.toString())
            else -> null
        }
    }
}