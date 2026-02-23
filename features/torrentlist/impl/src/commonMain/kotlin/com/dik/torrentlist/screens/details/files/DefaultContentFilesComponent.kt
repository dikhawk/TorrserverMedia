package com.dik.torrentlist.screens.details.files

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.converter.toReadableSize
import com.dik.torrentlist.screens.model.ContentFileUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultContentFilesComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val onClickPlayFile: suspend (contentFile: ContentFileUiState) -> Unit
) : ContentFilesComponent, ComponentContext by componentContext {

    private val _uiState = MutableStateFlow(ContentFilesState())
    override val uiState: StateFlow<ContentFilesState> = _uiState.asStateFlow()


    override fun showFiles(contentFiles: List<ContentFileUiState>) {
        _uiState.update { it.copy(files = prepareFiles(contentFiles)) }
    }

    private fun prepareFiles(contentFiles: List<ContentFileUiState>): Map<String, List<FileState>> {
        val directories = mutableMapOf<String, MutableList<FileState>>()

        contentFiles.forEach { file ->
            val result = file.path.split("/")
            val directory = result.dropLast(1).joinToString("/")

            if (directories[directory] == null) directories[directory] = mutableListOf()

            directories[directory]?.add(
                FileState(
                    contentFile = file,
                    name = result.last(),
                    size = file.length.toReadableSize()
                )
            )
        }

        return directories
    }

    override fun onClickItem(contentFile: ContentFileUiState) {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            onClickPlayFile(contentFile)
        }
    }
}