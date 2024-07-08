package com.dik.torrentlist.screens.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.dik.torrentlist.ChildConfig
import com.dik.torrentlist.screens.details.DefaultDetailsComponent
import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.DefaultMainComponent
import com.dik.torrentlist.screens.main.MainComponent

internal class DefaultRootComponent(componentContext: ComponentContext) : RootComponent,
    ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Main,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    override fun mainComponent(): MainComponent {
        return DefaultMainComponent(childContext("main_component"))
    }

    override fun detailisComponent(): DetailsComponent {
        return DefaultDetailsComponent(childContext("details_component"))
    }

    private fun childFactory(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        is ChildConfig.Details -> RootComponent.Child.Details(detailisComponent())
        ChildConfig.Main -> RootComponent.Child.Main(mainComponent())
    }
}