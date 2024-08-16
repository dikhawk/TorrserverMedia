package com.dik.settings.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.settings.di.inject
import com.dik.settings.main.DefaultMainComponent
import com.dik.settings.main.MainComponent
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi

internal class DefaultRootComponent(
    context: ComponentContext,
    private val serverSettingsApi: ServerSettingsApi = inject(),
    private val dispatchers: AppDispatchers = inject(),
    private val appSettings: AppSettings = inject(),
    private val torrserverStuffApi: TorrserverStuffApi = inject(),
    private val torrserverCommands: TorrserverCommands = inject(),
    private val onFinish: () -> Unit,
) : RootComponent, ComponentContext by context {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Main,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    override fun onClickBack() {
        navigation.pop()
    }

    override fun mainComponent(): MainComponent =
        DefaultMainComponent(
            childContext("main_component"),
            onFinish = onFinish,
            serverSettingsApi = serverSettingsApi,
            dispatchers = dispatchers,
            appSettings = appSettings,
            torrserverStuffApi = torrserverStuffApi,
            torrserverCommands = torrserverCommands
        )

    private fun childFactory(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        ChildConfig.Main -> RootComponent.Child.Main(mainComponent())
    }
}