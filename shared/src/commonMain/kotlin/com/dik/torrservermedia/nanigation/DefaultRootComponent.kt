package com.dik.torrservermedia.nanigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.setLocalization
import com.dik.settings.SettingsFeatureApi
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrservermedia.di.inject
import com.dik.torrservermedia.nanigation.RootComponent.Child
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val initialConfiguration: ChildConfig = ChildConfig.TorrentList,
    private val featureTorrentListApi: TorrentListFeatureApi = inject(),
    private val featureSettingsApi: SettingsFeatureApi = inject(),
    private val torrServerApi: TorrserverApi = inject(),
    private val dispatchers: AppDispatchers = inject(),
    private val appSettings: AppSettings = inject(),
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = initialConfiguration,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    private val scope = CoroutineScope(dispatchers.defaultDispatcher() + SupervisorJob())

    override fun onBackClicked() {
        navigation.pop()
    }

    override fun startServer() {
        scope.launch {
            setLocalization(appSettings.language)
            torrServerApi.torrserverManager().start().last()
        }
    }

    override fun stopServer() {
        scope.launch {
            torrServerApi.torrserverManager().stop().last()
        }
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