package com.dik.torrservermedia

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.dik.torrservermedia.di.KoinModules
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import org.koin.mp.KoinPlatform

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KoinModules.init()

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            featureTorrentList = inject()
        )

        enableEdgeToEdge()
        setContent { RootUi(component = root) }
    }
}