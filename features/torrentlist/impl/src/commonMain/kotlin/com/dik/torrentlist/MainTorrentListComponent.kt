package com.dik.torrentlist

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.dik.torrentlist.details.DetailsComponent
import com.dik.torrentlist.list.TorrentListComponent

interface MainTorrentListComponent {

    val childStack: Value<ChildStack<*, Child>>

    fun torrentListComponent(): TorrentListComponent

    sealed interface Child {
        class TorrentList(val component: TorrentListComponent) : Child
        class Details(val component: DetailsComponent) : Child
    }
}