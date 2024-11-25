package com.dik.torrserverapi.service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.dik.torrserverapi.model.TorrserverServiceManager

internal class TorrserverServiceManagerImpl(
    private val context: Context
) : TorrserverServiceManager {

    override fun startService() {
        val intent = Intent(context, TorrserverService::class.java)
        intent.action = TorrserverServiceAction.START_SERVICE.asString

        startService(intent)
    }

    override fun stopService() {
        val intent = Intent(context, TorrserverService::class.java)
        intent.action = TorrserverServiceAction.STOP_SERVICE.asString

        startService(intent)
    }

    private fun startService(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}