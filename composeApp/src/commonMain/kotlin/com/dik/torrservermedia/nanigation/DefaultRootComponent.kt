package com.dik.torrservermedia.nanigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.dik.settings.SettingsFeatureApi
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrservermedia.nanigation.RootComponent.Child
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val featureTorrentListApi: TorrentListFeatureApi,
    private val featureSettingsApi: SettingsFeatureApi
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.TorrentList,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    override fun onBackClicked() {
        navigation.pop()
    }

    private fun childFactory(
        config: ChildConfig, componentContext: ComponentContext
    ): Child = when (config) {
        ChildConfig.TorrentList -> Child.TorrentList(
            composable = featureTorrentListApi.start().composableMain(componentContext)
        )

        ChildConfig.Settings -> Child.Settings(
            composable = featureSettingsApi.start()
                .composableMain(context = componentContext, onFinish = ::onBackClicked)
        )
    }
}

@Serializable
private sealed interface ChildConfig {
    @Serializable
    data object TorrentList : ChildConfig

    @Serializable
    data object Settings : ChildConfig
}