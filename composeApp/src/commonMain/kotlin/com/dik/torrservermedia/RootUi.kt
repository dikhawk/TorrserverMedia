package com.dik.torrservermedia

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.dik.torrentlist.MainTorrentListUi
import com.dik.torrservermedia.nanigation.RootComponent
import com.dik.torrservermedia.nanigation.RootComponent.Child.TorrentList

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootUi(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + scale()),
            onBack = component::onBackClicked,
        )
    ) { child ->
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White ) {
            when(val rootChild = child.instance) {
                is TorrentList -> rootChild.composable()
            }
        }
    }
}