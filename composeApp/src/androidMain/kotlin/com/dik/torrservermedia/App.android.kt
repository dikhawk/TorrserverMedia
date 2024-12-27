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
import com.dik.common.CurrentActivityProvider
import com.dik.common.platform.PlatformEvensManager
import com.dik.common.platform.intent.PlatformAction
import com.dik.common.platform.intent.PlatformIntent
import com.dik.torrserverapi.model.TorrserverServiceManager
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
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
    private val platformEvensManager: PlatformEvensManager = inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            featureTorrentListApi = inject(),
            featureSettingsApi = inject(),
        )

        lifecycleScope.launch {
            torrserverService.startService()
            emitSystemEvents(intent)
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
                notificationPermission.launchMultiplePermissionRequest()
            }
        }
    }

    //TODO перенести в отдельный класс
    private suspend fun emitSystemEvents(intent: Intent) {
        val systemEventsFlow = platformEvensManager.systemEventsFlow()
        val action = intent.action
        val data: Uri? = intent.data
        val type = intent.type
        val torrentType = "application/x-bittorrent"

        if (action == Intent.ACTION_VIEW && data != null) {
            if (type == torrentType) {
                //TODO сделать сохранение в cache директорию для content uri, после этого возвращать абсолютный путь до файла
                systemEventsFlow.emit(PlatformIntent(PlatformAction.ADD_TORRENT, data.toString()))
            }
        }
    }
}