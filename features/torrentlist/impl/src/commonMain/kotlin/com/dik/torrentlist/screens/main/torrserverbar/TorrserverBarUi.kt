package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.uikit.widgets.AppButton
import com.dik.uikit.widgets.AppCircleProgressIndicator
import com.dik.uikit.widgets.AppLinearProgressIndicator
import com.dik.uikit.widgets.AppNormaBoldlItalicText
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalVerticalSpacer
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_install_server
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_start_server
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_running

@Composable
internal fun TorrserverBarUi(
    component: TorrserverBarComponent,
    torrserverStatus: TorrserverStatus,
    modifier: Modifier = Modifier
) {
    val uiState by component.uiState.collectAsState()
    Column(modifier = modifier) {
        when (torrserverStatus) {
            TorrserverStatus.RUNNING -> {
                RunningServer()
            }

            TorrserverStatus.NOT_STARTED -> {
                RestartTorrserver(onClickRestart = { component.onClickStartServer() })
            }

            TorrserverStatus.NOT_INSTALLED -> {
                InstallServer(
                    isShowProgress = uiState.isShowProgress,
                    progressValue = uiState.progressValue,
                    onClickInstall = { component.onClickInstallServer() }
                )
            }

            else -> {
                AppNormalBoldText(torrserverStatus.toString())
            }
        }
    }
}

@Composable
private fun RunningServer(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AppCircleProgressIndicator()
        AppNormalVerticalSpacer()
        AppNormalBoldText(stringResource(Res.string.main_torrserver_bar_msg_running))
    }
}

@Composable
private fun InstallServer(
    modifier: Modifier = Modifier,
    message: String? = null,
    progressValue: Float = 0f,
    isShowProgress: Boolean = false,
    onClickInstall: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isShowProgress) {
            AppNormaBoldlItalicText(text = "$progressValue %")
            AppLinearProgressIndicator(progress = { progressValue / 100f })
        }

        if (message != null) AppNormalBoldText(text = message)

        AppNormalVerticalSpacer()

        AppButton(
            modifier = modifier,
            enabled = !isShowProgress,
            text = stringResource(Res.string.main_torrserver_bar_button_install_server),
            onClick = onClickInstall
        )
    }
}

@Composable
private fun RestartTorrserver(modifier: Modifier = Modifier, onClickRestart: () -> Unit) {
    AppButton(
        modifier = modifier,
        text = stringResource(Res.string.main_torrserver_bar_button_start_server),
        onClick = onClickRestart
    )
}