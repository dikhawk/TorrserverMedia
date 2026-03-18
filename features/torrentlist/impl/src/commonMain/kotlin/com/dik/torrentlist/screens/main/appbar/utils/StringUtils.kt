package com.dik.torrentlist.screens.main.appbar.utils

import androidx.compose.runtime.Composable
import com.dik.torrentlist.screens.main.torrserverbar.InstallingState
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_installing_torrserver
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_server_is_installed
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_server_not_installed
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_server_preparing
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_server_reinstall

@Composable
internal fun InstallingState.asString(): String {
    return when (this) {
        InstallingState.Installed -> stringResource(Res.string.main_torrserver_bar_msg_server_is_installed)
        is InstallingState.Installing -> stringResource(Res.string.main_torrserver_bar_msg_installing_torrserver)
        InstallingState.NotInstalled -> stringResource(Res.string.main_torrserver_bar_msg_server_not_installed)
        InstallingState.Preparing -> stringResource(Res.string.main_torrserver_bar_msg_server_preparing)
        InstallingState.Reinstalling -> stringResource(Res.string.main_torrserver_bar_msg_server_reinstall)
        is InstallingState.Error -> this.msg
        is InstallingState.Unknown -> this.msg
    }
}