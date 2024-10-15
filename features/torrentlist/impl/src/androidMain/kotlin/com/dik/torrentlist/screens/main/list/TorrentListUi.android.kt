package com.dik.torrentlist.screens.main.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dik.uikit.widgets.AppNormalBoldText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_is_empty

@Composable
internal actual fun TorrentListUi(
    component: TorrentListComponent,
    modifier: Modifier
) {
    val uiState = component.uiState.collectAsState()

    Scaffold { paddingValues ->
        when {
            uiState.value.torrents.isEmpty() -> EmptyListStub()
            else -> Torrents(modifier.padding(paddingValues))
        }
    }
}

@Preview
@Composable
internal fun Torrents(modifier: Modifier = Modifier) {
    Text("Torrent list")
}

@Composable
fun EmptyListStub(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AppNormalBoldText(stringResource(Res.string.main_torrent_list_is_empty))
    }
}