package com.dik.torrentlist.screens.details.files

import com.arkivanov.decompose.ComponentContext
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.player.playContent
import com.dik.torrserverapi.ContentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class DefaultContentFilesComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val cmdRunner: CmdRunner,
    private val appSettings: AppSettings
) : ContentFilesComponent, ComponentContext by componentContext {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())
    private val _uiState = MutableStateFlow<ContentFilesState>(ContentFilesState())
    override val uiState: StateFlow<ContentFilesState> = _uiState.asStateFlow()


    override fun showFiles(contentFiles: List<ContentFile>) {
        _uiState.value.files.clear()
        _uiState.value.files.addAll(contentFiles)
    }

    override fun onClickItemPlay(contentFile: ContentFile) {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            contentFile.url.playContent(appSettings.defaultPlayer)
//            cmdRunner.runCmdCommand("vlc '${contentFile.url}'")
        }
    }
}