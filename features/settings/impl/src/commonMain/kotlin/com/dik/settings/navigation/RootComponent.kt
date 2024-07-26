package com.dik.settings.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.dik.settings.main.MainComponent

internal interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun onClickBack()

    fun mainComponent(): MainComponent

    sealed interface Child {
        class Main(val component: MainComponent) : Child
    }
}