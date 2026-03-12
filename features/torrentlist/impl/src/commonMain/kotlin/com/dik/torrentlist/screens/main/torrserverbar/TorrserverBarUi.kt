package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dik.torrentlist.domain.ServerStatus
import com.dik.torrentlist.screens.main.appbar.utils.asString
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
    serverStatus: ServerStatus,
    modifier: Modifier = Modifier
) {
    val uiState by component.uiState.collectAsState()
    Column(modifier = modifier) {
        when (serverStatus) {
            ServerStatus.General.Running -> {
                RunningServer()
            }

            ServerStatus.General.Stopped -> {
                RestartTorrserver(onClickRestart = { component.onClickStartServer() })
            }

            ServerStatus.General.NotInstalled -> {
                InstallServer(
                    installingState = uiState.installingState,
                    onClickInstall = { component.onClickInstallServer() }
                )
            }

            else -> {
                AppNormalBoldText(serverStatus.toString())
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
    installingState: InstallingState,
    onClickInstall: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        AppNormalBoldText(text = installingState.asString())

        when(installingState) {
            is InstallingState.Installing -> {
                AppNormalVerticalSpacer()
                AppNormaBoldlItalicText(text = "${installingState.percent} %")
                AppNormaBoldlItalicText(text = "${installingState.currentBytes}/${installingState.totalBytes}")
                AppLinearProgressIndicator(progress = { installingState.progress })
            }
            InstallingState.NotInstalled -> {
                AppNormalVerticalSpacer()
                AppButton(
                    modifier = modifier,
                    text = stringResource(Res.string.main_torrserver_bar_button_install_server),
                    onClick = onClickInstall
                )
            }
            else -> { }
        }
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