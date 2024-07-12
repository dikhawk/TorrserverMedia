package com.dik.torrentlist.screens.details.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.torrserverapi.ContentFile
import com.dik.uikit.widgets.AppNormalBoldText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_content_files_files_list

@Composable
internal fun ContentFilesUi(component: ContentFilesComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Column {
        AppNormalBoldText(
            text = stringResource(Res.string.main_content_files_files_list),
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(uiState.value.files) { file ->
                ContentFileItem(file = file, onClickItem = { component.onClickItemPlay(it) })
            }
        }
    }
}

@Composable
private fun ContentFileItem(
    file: ContentFile,
    onClickItem: (ContentFile) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = file.path,
        modifier = modifier.fillMaxWidth().clickable { onClickItem(file) }.padding(4.dp)
    )
}