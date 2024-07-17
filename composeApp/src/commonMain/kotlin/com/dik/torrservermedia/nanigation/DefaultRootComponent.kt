package com.dik.torrservermedia.nanigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrservermedia.nanigation.RootComponent.Child
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext, private val featureTorrentListApi: TorrentListFeatureApi
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
    ): RootComponent.Child = when (config) {
        is ChildConfig.TorrentList -> Child.TorrentList(
            composable = featureTorrentListApi.start().composableMain(componentContext)
        )
    }
}

@Serializable
private sealed interface ChildConfig {
    @Serializable
    object TorrentList : ChildConfig
}