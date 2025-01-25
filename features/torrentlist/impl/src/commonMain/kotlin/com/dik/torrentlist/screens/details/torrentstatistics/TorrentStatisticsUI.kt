package com.dik.torrentlist.screens.details.torrentstatistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.widgets.AppNormalHorizontalSpacer
import com.dik.uikit.widgets.AppNormalText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.ic_download
import torrservermedia.features.torrentlist.impl.generated.resources.ic_people

@Composable
fun TorrentStatisticsUI(component: TorrentStatisticsComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Row {
        StatisticContainer(value = uiState.value.torrentStatus)

        Spacer(modifier.weight(1f))

        StatisticContainer(value = uiState.value.downloadSpeed, icon = Res.drawable.ic_download)

        Spacer(modifier.weight(1f))

        StatisticContainer(value = uiState.value.activePeers, icon = Res.drawable.ic_people)
    }
}

@Composable
private fun StatisticContainer(
    value: String,
    icon: DrawableResource? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.primaryContainer)
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
    ) {
        if (icon != null) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(icon),
                contentDescription = null
            )
            AppNormalHorizontalSpacer()
        }
        AppNormalText(value)
    }
}