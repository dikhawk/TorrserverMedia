package com.dik.torrentlist.list

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

internal class DefaultTorrentListComponent(
    context: ComponentContext,
    private val onTorrentClick: (Torrent) -> Unit
) : ComponentContext by context,
    TorrentListComponent {
    override val torrenListState: StateFlow<TorrentListState>
        get() = TODO("Not yet implemented")

    override fun onClickItem(torrent: Torrent) {
        onTorrentClick(torrent)
    }
}