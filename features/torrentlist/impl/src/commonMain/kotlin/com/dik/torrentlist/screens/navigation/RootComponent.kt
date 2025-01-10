package com.dik.torrentlist.screens.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.MainComponent

internal interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    fun mainComponent(componentContext: ComponentContext, pathToTorrent: String? = null): MainComponent

    fun detailsComponent(
        componentContext: ComponentContext,
        torrentHash: String? = null
    ): DetailsComponent

    sealed interface Child {
        class Main(val component: MainComponent) : Child
        class Details(
            val component: DetailsComponent,
            val torrentHash: String,
            val poster: String
        ) : Child

        class Settings(val composable: @Composable () -> Unit) : Child
    }
}