package com.dik.torrentlist.screens.details.files

import androidx.compose.runtime.Stable
import com.dik.torrserverapi.ContentFile
import kotlinx.coroutines.flow.StateFlow

internal interface ContentFilesComponent {

    val uiState: StateFlow<ContentFilesState>

    fun showFiles(contentFiles: List<ContentFile>)
    fun onClickItem(contentFile: ContentFile)
}

@Stable
internal data class ContentFilesState(
    val files: Map<String, List<File>> = emptyMap()
)

internal data class File(
    val name: String,
    val size: String,
    val contentFile: ContentFile
)