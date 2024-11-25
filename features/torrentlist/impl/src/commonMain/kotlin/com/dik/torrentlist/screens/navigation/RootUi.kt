package com.dik.torrentlist.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.dik.torrentlist.screens.details.DetailsUi
import com.dik.torrentlist.screens.main.MainAdaptiveUi

@Composable
internal fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    Children(
        stack = component.childStack,
        modifier = modifier
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Main -> MainAdaptiveUi(instance.component)
            is RootComponent.Child.Details -> DetailsUi(instance.component)
            is RootComponent.Child.Settings -> instance.composable.invoke()
        }
    }
}