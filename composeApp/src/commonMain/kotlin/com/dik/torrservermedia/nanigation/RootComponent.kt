package com.dik.torrservermedia.nanigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner

interface RootComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed interface Child {
        class TorrentList(val composable: @Composable () -> Unit) : Child
        class Settings(val composable: @Composable () -> Unit) : Child
    }
}