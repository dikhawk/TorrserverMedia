package com.dik.torrentlist.screens.details.files

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.model.ContentFileUiState
import kotlinx.coroutines.flow.StateFlow

internal interface ContentFilesComponent {

    val uiState: StateFlow<ContentFilesState>

    fun showFiles(contentFiles: List<ContentFileUiState>)
    fun onClickItem(contentFile: ContentFileUiState)
}

@Stable
internal data class ContentFilesState(
    val files: Map<String, List<FileState>> = emptyMap()
)

@Stable
internal data class FileState(
    val name: String,
    val size: String,
    val contentFile: ContentFileUiState
)