package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.AppButton
import com.dik.uikit.widgets.AppNormalText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_install_server
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_start_server
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_update_server

@Composable
internal fun TorrserverBarUi(component: TorrserverBarComponent, modifier: Modifier = Modifier) {
    val uiState by component.uiState.collectAsState()

    Column(modifier = modifier) {
        ServerStatusWithButtons(
            statusText = uiState.serverStatusText,
            content = {
                if (!uiState.isServerInstalled && !uiState.isShowProgress) {
                    AppButton(
                        text = stringResource(Res.string.main_torrserver_bar_button_install_server),
                        enabled = !uiState.isShowProgress,
                        onClick = { component.onClickInstallServer() }
                    )
                }
                if (!uiState.isServerStarted && uiState.isServerInstalled && !uiState.isShowProgress) {
                    AppButton(
                        text = stringResource(Res.string.main_torrserver_bar_button_start_server),
                        enabled = !uiState.isShowProgress,
                        onClick = { component.onClickStartServer() }
                    )
                }
            }
        )

        if (uiState.isShowProgress) {
            Spacer(modifier = Modifier.padding(8.dp))
            LinearProgressIndicator(
                progress = { uiState.progressUpdate },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



@Composable
private fun ServerStatusWithButtons(
    statusText: String,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        AppNormalText(text = statusText)
        Spacer(modifier = Modifier.padding(8.dp))
        content()
    }
}