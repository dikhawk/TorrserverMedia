package com.dik.torrentlist.screens.components.bufferization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dik.uikit.widgets.AppMiddleVerticalSpacer
import com.dik.uikit.widgets.AppNormaBoldlItalicText
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalHorizontalSpacer
import com.dik.uikit.widgets.AppNormalItalicText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppNormalVerticalSpacer
import com.dik.uikit.widgets.AppTextButton
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_active_pears
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_buffer_size
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_button_cancel
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_downloading_speed
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_file_size
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_pears_mask

@Composable
internal fun BufferizationUi(
    component: BufferizationComponent,
    modifier: Modifier = Modifier
) {
    val uiState by component.uiState.collectAsState()
    val localDensity = LocalDensity.current
    var bottomPadding by remember { mutableStateOf(0.dp) }

    Dialog(onDismissRequest = {}) {
        BoxWithConstraints {
            val maxHeight = maxHeight

            Card(modifier = modifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.heightIn(max = maxHeight - 100.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Header(
                            fileName = uiState.fileName,
                            title = uiState.title,
                            titleSecond = uiState.titleSecond
                        )

                        TorrentStatistics(
                            downloadSpeed = uiState.downloadSpeed,
                            fileSize = uiState.fileSize,
                            downloadProgress = uiState.downloadProgress,
                            activePeers = uiState.activePeers,
                            totalPeers = uiState.totalPeers,
                            downloadProgressText = uiState.downloadProgressText
                        )

                        if (uiState.overview.isNotEmpty()) {
                            Overview(
                                overview = uiState.overview,
                                modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(bottomPadding))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .onSizeChanged { size ->
                                bottomPadding = with(localDensity) { size.height.toDp() }
                            }
                            .padding(top = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        AppTextButton(
                            text = stringResource(Res.string.main_bufferization_button_cancel),
                            onClick = { component.onClickCancel() })
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    fileName: String,
    title: String,
    titleSecond: String
) {
    AppNormalText(text = fileName)
    if (title.isNotEmpty())
        AppNormalBoldText(title)

    if (titleSecond.isNotEmpty())
        AppNormaBoldlItalicText(titleSecond)

    AppMiddleVerticalSpacer()
}

@Composable
private fun TorrentStatistics(
    downloadSpeed: String,
    fileSize: String,
    downloadProgress: Float,
    activePeers: String,
    totalPeers: String,
    downloadProgressText: String
) {
    Row {
        AppNormalItalicText(text = stringResource(Res.string.main_bufferization_downloading_speed))
        AppNormalHorizontalSpacer()
        AppNormalBoldText(text = downloadSpeed)
        Spacer(modifier = Modifier.weight(1f))
        AppNormalItalicText(text = stringResource(Res.string.main_bufferization_file_size))
        AppNormalHorizontalSpacer()
        AppNormalBoldText(text = fileSize)
    }

    AppNormalVerticalSpacer()
    LinearProgressIndicator(
        progress = { downloadProgress },
        modifier = Modifier.fillMaxWidth(),
    )
    AppNormalVerticalSpacer()
    Row {
        if (activePeers.isNotEmpty()) {
            AppNormalItalicText(text = stringResource(Res.string.main_bufferization_active_pears))
            AppNormalHorizontalSpacer()
            AppNormalBoldText(
                text = stringResource(Res.string.main_bufferization_pears_mask).format(
                    activePeers,
                    totalPeers
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (downloadProgressText.isNotEmpty()) {
            AppNormalItalicText(text = stringResource(Res.string.main_bufferization_buffer_size))
            AppNormalHorizontalSpacer()
            AppNormalBoldText(text = downloadProgressText)
        }
    }
}

@Composable
private fun Overview(
    overview: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        AppNormalVerticalSpacer()
        AppNormalItalicText(text = overview)
    }
}