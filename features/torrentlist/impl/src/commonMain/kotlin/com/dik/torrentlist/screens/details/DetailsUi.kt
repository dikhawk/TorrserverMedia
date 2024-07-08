package com.dik.torrentlist.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.dik.torrentlist.screens.details.files.ContentFilesUi
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsUI

@Composable
internal fun DetailsUi(component: DetailsComponent, modifier: Modifier = Modifier) {
    Column {
        TorrentStatisticsUI(component = component.torrentStatisticsComponent)
        ContentFilesUi(component = component.contentFilesComponent, modifier = Modifier.background(Color.Gray))
    }
}