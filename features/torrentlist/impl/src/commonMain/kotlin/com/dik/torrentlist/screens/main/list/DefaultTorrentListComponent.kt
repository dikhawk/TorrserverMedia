package com.dik.torrentlist.screens.main.list

import com.arkivanov.decompose.ComponentContext
import com.dik.common.Result
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.AddTorrentResult
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultTorrentListComponent(
    context: ComponentContext,
    private val onTorrentClick: (Torrent) -> Unit,
    private val torrentApi: TorrentApi,
    private val addTorrentFile: AddTorrentFile,
    private val componentScope: CoroutineScope,
    private val fileUtils: FileUtils
) : ComponentContext by context, TorrentListComponent {

    private val _uiState: MutableStateFlow<TorrentListState> = MutableStateFlow(TorrentListState())
    override val uiState: StateFlow<TorrentListState> = _uiState.asStateFlow()
    private var torrentListObserver: Job? = null

    init {
        componentScope.launch {
            observeTorrentList()
        }
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
        _uiState.update { it.copy(isShowProgress = true) }
        componentScope.launch {
            val tasks = mutableListOf<Deferred<AddTorrentResult>>()
            paths.forEach { uri ->
                val path = fileUtils.uriToPath(uri)
                tasks.add(async { addTorrentFile.invoke(path) })
            }
            tasks.awaitAll()
            _uiState.update { it.copy(isShowProgress = false) }
        }
    }

    private fun observeTorrentList() {
        torrentListObserver?.cancel()
        torrentListObserver = componentScope.launch {
            while (true) {
                when(val result = torrentApi.getTorrents()) {
                    is Result.Error -> _uiState.update {
                        it.copy(error = result.error.toString())
                    }
                    is Result.Success -> {
                        _uiState.value.torrents.clear()
                        _uiState.value.torrents.addAll(result.data)
                    }
                }
                delay(3000)
            }
        }
    }

    fun stopObservingTorrentList() {
        torrentListObserver?.cancel()
    }
}