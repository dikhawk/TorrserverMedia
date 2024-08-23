package com.dik.torrentlist.screens.details.files

import com.arkivanov.decompose.ComponentContext
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.player.PlayersCommands
import com.dik.common.player.platformPlayersCommands
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrserverapi.ContentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultContentFilesComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val cmdRunner: CmdRunner,
    private val appSettings: AppSettings,
    private val playersCommands: PlayersCommands = platformPlayersCommands()
) : ContentFilesComponent, ComponentContext by componentContext {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())
    private val _uiState = MutableStateFlow<ContentFilesState>(ContentFilesState())
    override val uiState: StateFlow<ContentFilesState> = _uiState.asStateFlow()


    override fun showFiles(contentFiles: List<ContentFile>) {
        _uiState.update { it.copy(files = prepareFiles(contentFiles)) }
    }

    private fun prepareFiles(contentFiles: List<ContentFile>): Map<String, List<File>> {
        val directories = mutableMapOf<String, MutableList<File>>()

        contentFiles.forEach { file ->
            val result = file.path.split("/")
            val dierctory = result.dropLast(1).joinToString("/")

            if (directories[dierctory] == null) directories[dierctory] = mutableListOf()

            directories[dierctory]?.add(File(
                id = file.id,
                name = result.last(),
                size = file.length.toReadableSize(),
                isViewed = file.isViewved,
                path = file.path,
                url = file.url
            ))
        }

        return directories
    }

    override fun onClickItemPlay(path: String, url: String) {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            playersCommands.playFile(
                fileName = path, fileUrl = url, player = appSettings.defaultPlayer
            )
        }
    }
}