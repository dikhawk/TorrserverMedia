package com.dik.torrentlist.screens.main.list

import com.arkivanov.decompose.ComponentContext
import com.dik.common.Result
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileErrors
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.screens.mappers.toTorrentUiStateList
import com.dik.torrentlist.screens.model.TorrentUiState
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.api.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultTorrentListComponent(
    context: ComponentContext,
    private val onTorrentClick: (TorrentUiState) -> Unit,
    private val onNavigateToDetails: (TorrentUiState) -> Unit,
    private val onTorrentsIsEmpty: (Boolean) -> Unit,
    private val torrentApi: TorrentApi,
    private val addTorrentFileUseCase: AddTorrentFileUseCase,
    private val addMagnetLinkUseCase: AddMagnetLinkUseCase,
    private val componentScope: CoroutineScope,
    private val fileUtils: FileUtils
) : ComponentContext by context, TorrentListComponent {


    private val observeTorrents = torrentApi.observeTorrents()
        .onStart { _uiState.update { it.copy(isShowProgress = true) } }
        .distinctUntilChanged()
        .map { result ->
            if (result is Result.Success) {
                onTorrentsIsEmpty(result.data.isEmpty())
                _uiState.value.copy(
                    torrents = result.data.toTorrentUiStateList(),
                    isShowProgress = false
                )
            } else {
                val error = result as Result.Error
                _uiState.value.copy(error = error.error.toString(), isShowProgress = false)
            }
        }

    private val _uiState: MutableStateFlow<TorrentListState> = MutableStateFlow(TorrentListState())
    override val uiState: StateFlow<TorrentListState> =
        merge(_uiState, observeTorrents).stateIn(
            scope = componentScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TorrentListState(isShowProgress = true)
        )

    override fun onClickItem(torrent: TorrentUiState) {
        onTorrentClick.invoke(torrent)
    }

    override fun onNavigateToDetails(torrent: TorrentUiState) {
        onNavigateToDetails.invoke(torrent)
    }

    override fun onClickDeleteItem(torrent: TorrentUiState) {
        componentScope.launch {
            torrentApi.removeTorrent(torrent.hash)
        }
    }

    override fun addTorrents(paths: List<String>) {
        componentScope.launch {
            _uiState.update { it.copy(isShowProgress = true) }
            val tasks = mutableListOf<Deferred<Result<Torrent, AddTorrentFileErrors>>>()

            paths.forEach { uri ->
                val path = fileUtils.uriToPath(uri)
                tasks.add(async { addTorrentFileUseCase.invoke(path) })
            }

            tasks.awaitAll()
            _uiState.update { it.copy(isShowProgress = false) }
        }
    }

    override fun addMagnet(magnetLink: String) {
        _uiState.update { it.copy(isShowProgress = true) }
        componentScope.launch {
            addMagnetLinkUseCase.invoke(magnetLink)
                .onSuccess {
                    // Список обновится автоматически через observeTorrentsList
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(error = error.toString())
                    }
                }
            _uiState.update { it.copy(isShowProgress = false) }
        }
    }
}