package com.dik.settings.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.dik.settings.main.MainUi


@Composable
internal fun RootUi(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.childStack,
        modifier = modifier
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Main -> MainUi(instance.component)
        }
    }
}