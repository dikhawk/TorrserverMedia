package com.dik.torrentlist.screens.main.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.torrserverapi.Torrent
import com.dik.torrserverapi.server.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
    private val dispatchers: AppDispatchers,
) : ComponentContext by context, TorrentListComponent {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())
    private val _uiState: MutableStateFlow<TorrentListState> = MutableStateFlow(TorrentListState())
    override val uiState: StateFlow<TorrentListState> = _uiState.asStateFlow()

    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
        torrntsList()
    }

    override fun onClickItem(torrent: Torrent) {
        onTorrentClick(torrent)
    }

    private fun torrntsList() {
        componentScope.launch {
            while (true) {
                val torrentsListResult = torrentApi.getTorrentsList()

                when(val result = torrentsListResult) {
                    is Result.Error -> _uiState.update { it.copy(error = result.error.toString()) }
                    is Result.Success -> {
                        _uiState.value.torrents.clear()
                        _uiState.value.torrents.addAll(result.data)
                    }
                }
                delay(2000)
            }
        }
    }
}