package com.dik.torrentlist.screens.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.dik.torrentlist.screens.details.DetailsUi
import com.dik.torrentlist.screens.main.MainAdaptiveUi

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.childStack.subscribeAsState()

    SharedTransitionLayout {
        Children(
            stack = stack,
            modifier = modifier,
            animation = stackAnimation(fade() + scale()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Main -> MainAdaptiveUi(
                    instance.component,
                    stack.active.instance is RootComponent.Child.Main
                )

                is RootComponent.Child.Details -> DetailsUi(
                    component = instance.component,
                    torrentHash = instance.torrentHash,
                    poster = instance.poster,
                    isVisible = stack.active.instance is RootComponent.Child.Details
                )

                is RootComponent.Child.Settings -> instance.composable.invoke()
            }
        }
    }
}