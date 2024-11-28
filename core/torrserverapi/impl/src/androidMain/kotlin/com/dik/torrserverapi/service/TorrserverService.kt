package com.dik.torrserverapi.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.ResultProgress
import com.dik.common.utils.successResult
import com.dik.torrserverapi.di.inject
import com.dik.torrserverapi.server.TorrserverCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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

    private fun createNotification(text: String): Notification {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("TorrServer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
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
}

enum class TorrserverServiceAction(val asString: String) {
    START_SERVICE("start_service"),
    STOP_SERVICE("stop_service")
}