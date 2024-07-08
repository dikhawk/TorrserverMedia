package com.dik.torrentlist.screens.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.MainComponent

internal interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    fun mainComponent(): MainComponent

    fun detailisComponent(): DetailsComponent

    sealed interface Child {
        class Main(val component: MainComponent) : Child
        class Details(val component: DetailsComponent) : Child
    }
}