package com.dik.torrservermedia

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.dik.common.platform.WindowAdaptiveObserver
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.RootComponent
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier,
    windowAdaptiveObserver: WindowAdaptiveObserver = inject()
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()

    Children(
        stack = component.stack,
        modifier = modifier,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + scale()),
            onBack = component::onBackClicked,
        )
    ) { child ->
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            when (val rootChild = child.instance) {
                is RootComponent.Child.TorrentList -> rootChild.composable()
                is RootComponent.Child.Details -> rootChild.composable()
                is RootComponent.Child.Settings -> rootChild.composable()
            }
        }
    }

    LaunchedEffect(windowAdaptiveInfo) {
        windowAdaptiveObserver.windowAdaptiveFlow().update { windowAdaptiveInfo }
    }
}