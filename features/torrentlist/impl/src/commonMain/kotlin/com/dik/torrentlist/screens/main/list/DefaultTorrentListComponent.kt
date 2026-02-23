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
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

    private val _uiState: MutableStateFlow<TorrentListState> = MutableStateFlow(TorrentListState())
    override val uiState: StateFlow<TorrentListState> = _uiState.asStateFlow()


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
        _uiState.update { it.copy(isShowProgress = true) }
        componentScope.launch {
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
    
    override fun observeTorrentsList(): Flow<List<TorrentUiState>> =
        torrentApi.observeTorrents().flatMapLatest { result ->
            if (result is Result.Success) {
                flow {
                    emit(result.data.toTorrentUiStateList())
                }
            } else {
                awaitCancellation()
            }
        }
}