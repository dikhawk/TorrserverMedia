package com.dik.torrentlist.screens.main.appbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.utils.successResult
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.screens.main.appbar.utils.defaultFilePickerDirectory
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverStuffApi
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.add_torrent_dialog_title
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_error_invalid_magnet

internal class DefaultMainAppBarComponent(
    context: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val torrentApi: TorrentApi,
    private val magnetApi: MagnetApi,
    private val torrserverStuffApi: TorrserverStuffApi,
    private val searchTheMovieDbApi: SearchTheMovieDbApi,
    private val openSettingsScreen: () -> Unit,
) : MainAppBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(MainAppBarState())
    override val uiState: StateFlow<MainAppBarState> = _uiState.asStateFlow()

    init {
        observeServerStatus()
    }

    override fun onClickAddTorrent() {
        if (!_uiState.value.isServerStarted) return

        componentScope.launch(dispatchers.defaultDispatcher()) {
            val fileType = PickerType.File(extensions = listOf("torrent"))

            val file = FileKit.pickFile(
                type = fileType,
                mode = PickerMode.Single,
                title = getString(Res.string.add_torrent_dialog_title),
                initialDirectory = defaultFilePickerDirectory()
            )
            val filePath = file?.path

            if (filePath != null) {
                val torrent = torrentApi.addTorrent(filePath).successResult()

                findAndAddThumbnail(torrent)
            }
        }
    }

    override fun openAddLinkDialog() {
        if (!_uiState.value.isServerStarted) return

        _uiState.update { it.copy(action = MainAppBarAction.ShowAddLinkDialog) }
    }

    override fun addLink() {
        if (!isValidMagnetLink(_uiState.value.link)) {
            _uiState.update { it.copy(errorLink = Res.string.main_add_dialog_error_invalid_magnet) }
            return
        }

        componentScope.launch(dispatchers.ioDispatcher()) {
            val torrent = magnetApi.addMagnet(magnetUrl = _uiState.value.link).successResult()

            findAndAddThumbnail(torrent)
            dismissDialog()
        }
    }

    override fun dismissDialog() {
        _uiState.update { it.copy(action = MainAppBarAction.Undefined) }
        clearLink()
    }

    override fun onLinkChaged(value: String) {
        _uiState.update { it.copy(link = value, errorLink = null) }
    }

    override fun clearLink() {
        _uiState.update { it.copy(link = "", errorLink = null) }
    }

    override fun openSettingsScreen() {
        if (!_uiState.value.isServerStarted) return

        openSettingsScreen.invoke()
    }

    private fun observeServerStatus() {
        componentScope.launch {
            torrserverStuffApi.observerServerStatus().collect { result ->
                val isStarted = !result.successResult().isNullOrEmpty()
                _uiState.update { it.copy(isServerStarted = isStarted) }
            }
        }
    }

    private fun isValidMagnetLink(magnetLink: String): Boolean {
        val magnetUriRegex = Regex("^magnet:\\?xt=urn:[a-z0-9]+:[a-zA-Z0-9]{32,40}(&.+)?$")
        return magnetUriRegex.matches(magnetLink)
    }

    private suspend fun findAndAddThumbnail(torrent: Torrent?) {
        if (torrent == null) return

        val firstFile = torrent.files.firstOrNull()
        val name = firstFile?.path?.fileName() ?: torrent.name
        val tv = parseFileNameTvShow(name)
        val movie = parseFileNameBase(torrent.name)
        val seasonNumber = tv?.seasons?.firstOrNull() ?: 0
        val episodeNumber = tv?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (seasonNumber > 0) && (episodeNumber > 0)
        val title = if (isTv) tv?.title else movie.title

        if(!title.isNullOrEmpty()) {
            val queryResult = searchTheMovieDbApi.multiSearching(title)
            val content = queryResult.successResult()?.firstOrNull() ?: return

            val thumbnail = when (content) {
                is Movie -> content.posterPath
                is TvShow -> content.posterPath
                else -> null
            }

            if (thumbnail.isNullOrEmpty()) return

            torrentApi.updateTorrent(torrent.copy(poster = thumbnail))
        }
    }
}