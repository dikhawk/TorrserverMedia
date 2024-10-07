package com.dik.torrentlist.screens.main.list

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.AddTorrentResult
import com.dik.torrentlist.utils.isFileExist
import com.dik.torrentlist.utils.uriToPath
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultTorrentListComponent(
    context: ComponentContext,
    private val onTorrentClick: (Torrent) -> Unit,
    private val torrentApi: TorrentApi,
    private val torrserverCommands: TorrserverCommands,
    private val addTorrentFile: AddTorrentFile,
    private val componentScope: CoroutineScope,
) : ComponentContext by context, TorrentListComponent {

    private val _uiState: MutableStateFlow<TorrentListState> = MutableStateFlow(TorrentListState())
    override val uiState: StateFlow<TorrentListState> = _uiState.asStateFlow()

    init {
        torrentsList()
        componentScope.launch { torrserverCommands.startServer() }
    }

    override fun onClickItem(torrent: Torrent) {
        onTorrentClick(torrent)
    }

    override fun onClickDeleteItem(torrent: Torrent) {
        componentScope.launch {
            torrentApi.removeTorrent(torrent.hash)
        }
    }

    override fun addTorrents(paths: List<String>) {
        componentScope.launch {
            val tasks = mutableListOf<Deferred<AddTorrentResult>>()
            paths.forEach { uri ->
                val path = uri.uriToPath()
                tasks.add(async { addTorrentFile.invoke(path) })
            }
            tasks.awaitAll()
        }
    }

    private fun torrentsList() {
        componentScope.launch {
            while (true) {
                when(val result = torrentApi.getTorrents()) {
                    is Result.Error -> _uiState.update { it.copy(error = result.error.toString()) }
                    is Result.Success -> {
                        _uiState.value.torrents.clear()
                        _uiState.value.torrents.addAll(result.data)
                    }
                }
                delay(3000)
            }
        }
    }
}