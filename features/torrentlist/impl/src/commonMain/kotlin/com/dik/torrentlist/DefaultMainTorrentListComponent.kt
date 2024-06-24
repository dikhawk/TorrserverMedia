package com.dik.torrentlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.dik.torrentlist.MainTorrentListComponent.Child
import com.dik.torrentlist.MainTorrentListComponent.Child.TorrentList
import com.dik.torrentlist.details.DefaultDetailsComponent
import com.dik.torrentlist.di.KoinModules
import com.dik.torrentlist.list.DefaultTorrentListComponent
import com.dik.torrentlist.list.TorrentListComponent
import com.dik.torrserverapi.di.TorrserverApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DefaultMainTorrentListComponent(
    componentContext: ComponentContext,
    private val torrserverApi: TorrserverApi = KoinModules.koin.get(),
    private val coroutineScope: CoroutineScope
) : ComponentContext by componentContext, MainTorrentListComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.List,
        handleBackButton = true,
        childFactory = ::childFactory
    )

    init {
        coroutineScope.launch {
            torrserverApi.torrserverStuffApi().checkUpdates()
        }
    }

    override fun torrentListComponent(): TorrentListComponent =
        //TODO Replace "Torrrent URL"
        DefaultTorrentListComponent(
            context = childContext(key = "torrent_list"),
            onTorrentClick = { navigation.push(ChildConfig.Details("Torrrent URL")) },
        )

    private fun childFactory(
        config: ChildConfig,
        componentContext: ComponentContext
    ): Child = when (config) {
        is ChildConfig.Details -> Child.Details(
            DefaultDetailsComponent(componentContext)
        )

        ChildConfig.List -> TorrentList(torrentListComponent())
    }
}