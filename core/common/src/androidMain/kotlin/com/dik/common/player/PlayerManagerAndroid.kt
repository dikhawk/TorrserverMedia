package com.dik.common.player

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.dik.common.player.model.PlatformApp

internal class PlayerManagerAndroid(
    private val context: Context
): PlayerManager {

    override suspend fun getAppsList(contentType: String): List<PlatformApp> {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = contentType
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        val packageManager = context.packageManager

        val appList = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).map {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(it.activityInfo.packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()

            PlatformApp(appId = it.activityInfo.packageName, appName)
        }

        return appList
    }

    override suspend fun openFile(appId: String, filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(filePath)).apply {
            setPackage(appId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}