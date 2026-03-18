package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dik.torrentlist.domain.ServerStatus
import com.dik.torrentlist.screens.main.appbar.utils.asString
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.widgets.AppButton
import com.dik.uikit.widgets.AppCircleProgressIndicator
import com.dik.uikit.widgets.AppLinearProgressIndicator
import com.dik.uikit.widgets.AppNormaBoldlItalicText
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalVerticalSpacer
import com.dik.uikit.widgets.AppSmallText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_install_server
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_reinstall
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_button_start_server
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_error
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_running
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_or

@Composable
internal fun TorrserverBarUi(
    component: TorrserverBarComponent,
    serverStatus: ServerStatus,
    modifier: Modifier = Modifier
) {
    val uiState by component.uiState.collectAsState()
    var isShowError by remember { mutableStateOf(true) }

    Column(modifier = modifier) {
        when (serverStatus) {
            ServerStatus.General.Running -> {
                RunningServer()
            }

            ServerStatus.General.Stopped -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RestartTorrserver(onClickRestart = { component.onClickStartServer() })
                    AppSmallText(text = stringResource(Res.string.main_torrserver_bar_or))
                    InstallServer(
                        textButton = stringResource(Res.string.main_torrserver_bar_button_reinstall),
                        installingState = uiState.reinstallingState,
                        onClickInstall = { component.onClickInstallServer() }
                    )
                }
            }

            ServerStatus.General.NotInstalled -> {
                InstallServer(
                    textButton = stringResource(Res.string.main_torrserver_bar_button_install_server),
                    installingState = uiState.installingState,
                    onClickInstall = { component.onClickInstallServer() }
                )
            }

            is ServerStatus.General.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isShowError) AppNormalBoldText(stringResource(Res.string.main_torrserver_bar_error) + serverStatus.msg)
                    AppNormalVerticalSpacer()
                    InstallServer(
                        textButton = stringResource(Res.string.main_torrserver_bar_button_reinstall),
                        installingState = uiState.reinstallingState,
                        onClickInstall = {
                            isShowError = false
                            component.onClickInstallServer()
                        }
                    )
                }
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
    textButton: String,
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
                    text = textButton,
                    onClick = onClickInstall
                )
            }
            InstallingState.Reinstalling -> {
                AppNormalVerticalSpacer()
                AppButton(
                    modifier = modifier,
                    text = textButton,
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

@Preview
@Composable
private fun InstallServerPreview() {
    AppTheme {
        InstallServer(
            textButton = "Install server",
            installingState = InstallingState.NotInstalled,
            onClickInstall = {}
        )
    }
}