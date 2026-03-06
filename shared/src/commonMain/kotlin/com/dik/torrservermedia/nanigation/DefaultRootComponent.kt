package com.dik.torrservermedia.nanigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.dik.settings.SettingsFeatureApi
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrservermedia.nanigation.RootComponent.Child
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val initialConfiguration: ChildConfig = ChildConfig.TorrentList,
    private val featureTorrentListApi: TorrentListFeatureApi,
    private val featureSettingsApi: SettingsFeatureApi
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = initialConfiguration,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    override fun onBackClicked() {
        navigation.pop()
    }

    override fun onOpenContent(config: ChildConfig) {
        navigation.replaceAll(config)
    }

    private fun childFactory(
        config: ChildConfig, componentContext: ComponentContext
    ): Child = when (config) {
        is ChildConfig.TorrentList -> Child.TorrentList(
            composable = featureTorrentListApi.start()
                .root(context = componentContext)
        )

        is ChildConfig.OpenMagnet -> Child.TorrentList(
            featureTorrentListApi.start()
                .openMagnet(context = componentContext, magnetLink = config.magnetLink)
        )

        is ChildConfig.OpenTorrent -> Child.TorrentList(
            featureTorrentListApi.start()
                .openTorrent(context = componentContext, pathToTorrent = config.pathToTorrent)
        )

        ChildConfig.Settings -> Child.Settings(
            composable = featureSettingsApi.start()
                .root(context = componentContext, onFinish = ::onBackClicked)
        )
    }
}

@Serializable
sealed interface ChildConfig {
    @Serializable
    data object TorrentList : ChildConfig

    @Serializable
    data class OpenMagnet(val magnetLink: String) : ChildConfig

    @Serializable
    data class OpenTorrent(val pathToTorrent: String) : ChildConfig

    @Serializable
    data object Settings : ChildConfig
}