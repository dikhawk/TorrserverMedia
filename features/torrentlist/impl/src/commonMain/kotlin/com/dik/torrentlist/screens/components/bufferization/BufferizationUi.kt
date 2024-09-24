package com.dik.torrentlist.screens.components.bufferization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dik.uikit.widgets.AppNormaBoldlItalicText
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalItalicText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppTextButton
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_button_cancel
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_downloading_speed

@Composable
internal fun BufferizationUi(component: BufferizationComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Dialog(onDismissRequest = {}) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)) {
                AppNormalText(text = uiState.value.fileName)
                if(uiState.value.title.isNotEmpty())
                    AppNormalBoldText(uiState.value.title)
                if(uiState.value.titleSecond.isNotEmpty())
                    AppNormaBoldlItalicText(uiState.value.titleSecond)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    AppNormalItalicText(text = stringResource(Res.string.main_bufferization_downloading_speed))
                    Spacer(modifier = Modifier.width(8.dp))
                    AppNormalBoldText(text = uiState.value.downloadSpeed)
                    Spacer(modifier = Modifier.weight(1f))
                    AppNormalBoldText(text = uiState.value.fileSize)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { uiState.value.downloadProgress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    AppNormalBoldText(text = uiState.value.downloadProgressText)
                    if(uiState.value.overview.isNotEmpty())
                        AppNormalItalicText(text = uiState.value.overview)

                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    AppTextButton(
                        text = stringResource(Res.string.main_bufferization_button_cancel),
                        onClick = { component.onClickCancel() })
                }
            }
        }
    }
}