package com.dik.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.dik.settings.navigation.DefaultRootComponent
import com.dik.settings.navigation.RootUi

class SettingsEntryImpl : SettingsEntry {

    override fun root(context: ComponentContext, onFinish: () -> Unit): @Composable () -> Unit = {
        RootUi(component = DefaultRootComponent(context = context, onFinish = onFinish))
    }
}