package com.dik.torrserverapi.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.utils.successResult
import com.dik.torrserverapi.di.inject
import com.dik.torrserverapi.impl.R
import com.dik.torrserverapi.server.TorrserverCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class TorrserverService : Service() {

    private val tag = "TorrserverService:"
    private val notificationId = System.currentTimeMillis().toInt()
    private val channelId = "channel_torrserver_service"
    private val channelTorrserver = "Torrserver"
    private val torrserverCommands: TorrserverCommands = inject()
    private val appDispatchers: AppDispatchers = inject()
    private val coroutineScope =
        CoroutineScope(appDispatchers.defaultDispatcher() + SupervisorJob())


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Logger.i("$tag Run Service")
        createServiceChannel()
        val notification = createNotification("$tag Running TorrServer")
        startForeground(notification)
    }

    override fun onDestroy() {
        Logger.i("$tag Stop Service")
        coroutineScope.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkNotificationPermission()

        when (intent?.action) {
            TorrserverServiceAction.START_SERVICE.asString -> starTorrServer()
            TorrserverServiceAction.STOP_SERVICE.asString -> stopTorrServer()
            TorrserverServiceAction.STOP_SERVICE_AND_CLOSE_APP.asString -> stopTorrServerAndCloseApp()
            else -> {
                Logger.e("$tag Invalid TorrserverServiceAction")
            }
        }
        return START_STICKY
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Logger.e("$tag Please granted POST_NOTIFICATIONS permission for TorrserverService")
        }
    }

    private fun starTorrServer() {
        coroutineScope.launch {
            torrserverCommands.startServer()
            val isServerStarted = torrserverCommands.isServerStarted().successResult() ?: false
            if (!isServerStarted) {
                stopForeground(notificationId)
                return@launch
            }

            Logger.i("$tag Service is working")
            startForeground(createNotification("Service is working"))
        }
    }


    /**
     * To enable the deeplink functionality in your application, follow these steps:
     *
     * 1. Define the deeplink configuration in the AndroidManifest.xml file for the target activity:
     *
     * <activity android:name=".MainActivity">
     *     <intent-filter>
     *         <action android:name="android.intent.action.VIEW" />
     *
     *         <category android:name="android.intent.category.DEFAULT" />
     *         <category android:name="android.intent.category.BROWSABLE" />
     *
     *         <!-- Specify the scheme and host for the deeplink -->
     *         <data android:scheme="app" android:host="torrserver" />
     *     </intent-filter>
     * </activity>
     *
     * 2. Handle the deeplink in the target activity (e.g., MainActivity):
     *
     * override fun onCreate(savedInstanceState: Bundle?) {
     *     super.onCreate(savedInstanceState)
     *     handleDeeplink(intent)
     * }
     *
     * override fun onNewIntent(intent: Intent?) {
     *     super.onNewIntent(intent)
     *     handleDeeplink(intent)
     * }
     *
     * private fun handleDeeplink(intent: Intent?) {
     *     intent?.data?.let { uri ->
     *         // Parse and handle the URI parameters, for example:
     *         val action = uri.getQueryParameter("action")
     *         // Perform specific actions based on the "action" parameter
     *     }
     * }
     *
     * After completing these steps, the deeplink "app://torrserver?action=open_home"
     * will correctly launch MainActivity and allow handling of any associated parameters.
     */
    private fun createNotification(text: String): Notification {
        val openHomeUri = Uri.parse(TorrserverServiceDeepLink.URL +
                "?${TorrserverServiceDeepLink.ACTION}=${TorrserverServiceDeepLink.OPEN_HOME_ACTION}")
        val openAppIntent = Intent(Intent.ACTION_VIEW, openHomeUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Для запуска Activity из Service
        }
        val exitIntent = Intent(this, TorrserverService::class.java)
        exitIntent.action = TorrserverServiceAction.STOP_SERVICE_AND_CLOSE_APP.asString

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("TorrServer")
            .setContentText(text)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(
                PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)
            )
            .addAction(
                android.R.drawable.ic_media_play,
                getString(R.string.torserver_stop_service),
                PendingIntent.getService(
                    this, 0, exitIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .build()
        return notification
    }

    private fun startForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun createServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel =
                NotificationChannel(
                    channelId,
                    channelTorrserver,
                    NotificationManager.IMPORTANCE_LOW
                )
            val notificationManger = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            chanel.setSound(null, null)
            notificationManger.createNotificationChannel(chanel)
        }
    }

    private fun stopTorrServer() {
        Logger.i("$tag Stop Service")
        coroutineScope.cancel()
        stopSelf()
    }

    private fun stopTorrServerAndCloseApp() {
        stopTorrServer()
        exitProcess(0)
    }
}

enum class TorrserverServiceAction(val asString: String) {
    START_SERVICE("start_service"),
    STOP_SERVICE("stop_service"),
    STOP_SERVICE_AND_CLOSE_APP("stop_service_and_close_app")
}

object TorrserverServiceDeepLink {
    const val URL = "app://torrserver?"
    const val ACTION = "action"

    const val OPEN_HOME_ACTION = "open_home"
}