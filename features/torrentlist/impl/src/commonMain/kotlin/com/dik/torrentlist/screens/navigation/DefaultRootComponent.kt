package com.dik.torrentlist.screens.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.player.PlayersCommands
import com.dik.settings.SettingsFeatureApi
import com.dik.torrentlist.di.inject
import com.dik.torrentlist.screens.details.DefaultDetailsComponent
import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.details.DetailsComponentScreenFormat
import com.dik.torrentlist.screens.main.DefaultMainComponent
import com.dik.torrentlist.screens.main.MainComponent
import com.dik.torrserverapi.ContentFile

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    private val initialConfiguration: ChildConfig = ChildConfig.Main,
    private val settingsFeatureApi: SettingsFeatureApi = inject(),
    private val appSettings: AppSettings = inject(),
    private val playersCommands: PlayersCommands = inject(),
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = initialConfiguration,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    override fun mainComponent(
        componentContext: ComponentContext,
        addContent: AddContent?
    ): MainComponent {
        return DefaultMainComponent(
            context = componentContext,
            openSettingsScreen = { navigation.pushNew(ChildConfig.Settings) },
            onClickPlayFile = { playFile(it) },
            navigateToDetails = { hash, poster ->
                navigation.pushNew(ChildConfig.Details(hash, poster))
            }
        ).apply {
            when (addContent) {
                is AddContent.Magnet -> addMagnetLinkAndShowDetails(addContent.magnetLink)
                is AddContent.Torrent -> addTorrentAndShowDetails(addContent.path)
                else -> return@apply
            }
        }
    }

    override fun detailsComponent(
        componentContext: ComponentContext,
        torrentHash: String?
    ): DetailsComponent {
        return DefaultDetailsComponent(
            componentContext = componentContext,
            onClickPlayFile = { _, contentFile -> playFile(contentFile) },
            screenFormat = DetailsComponentScreenFormat.SCREEN,
            onClickBack = { navigation.pop() }
        ).apply {
            if (torrentHash != null) {
                showDetails(torrentHash)
            }
        }
    }

    private suspend fun playFile(contentFile: ContentFile) {
        playersCommands.playFile(
            fileName = contentFile.path,
            fileUrl = contentFile.url,
            player = appSettings.defaultPlayer
        )
    }

    private fun childFactory(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        is ChildConfig.Main -> RootComponent.Child.Main(mainComponent(componentContext))

        is ChildConfig.AddMagnet -> RootComponent.Child.Main(
            mainComponent(componentContext, AddContent.Magnet(config.magnetLink))
        )

        is ChildConfig.AddTorrent ->
            RootComponent.Child.Main(mainComponent(componentContext, AddContent.Torrent(config.pathToTorrent))
        )

        is ChildConfig.Details -> RootComponent.Child.Details(
            component = detailsComponent(componentContext, config.torrentHash),
            torrentHash = config.torrentHash,
            poster = config.poster
        )

        ChildConfig.Settings -> RootComponent.Child.Settings(
            settingsFeatureApi.start()
                .root(context = componentContext, onFinish = { navigation.pop() })
        )
    }
}