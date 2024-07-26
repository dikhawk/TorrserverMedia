package com.dik.torrservermedia

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        KoinModules.init {
            androidContext(AndroidApp.INSTANCE)
        }
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            featureTorrentListApi = inject(),
            featureSettingsApi = inject()
        )

        enableEdgeToEdge()
        setContent { RootUi(component = root) }
    }
}