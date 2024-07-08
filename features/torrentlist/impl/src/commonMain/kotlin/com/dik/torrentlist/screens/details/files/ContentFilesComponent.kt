package com.dik.torrentlist.screens.details.files

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import com.dik.torrserverapi.ContentFile
import kotlinx.coroutines.flow.StateFlow

internal interface ContentFilesComponent {

    val uiState: StateFlow<ContentFilesState>

    fun showFiles(contentFiles: List<ContentFile>)
    fun onClickItemPlay(contentFile: ContentFile)
}

@Stable
data class ContentFilesState(
    val files: MutableList<ContentFile> = mutableStateListOf()
)