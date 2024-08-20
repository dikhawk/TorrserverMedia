package com.dik.torrentlist.screens.main.appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.dik.uikit.widgets.AppActionButton
import com.dik.uikit.widgets.AppTopBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.ic_add_24
import torrservermedia.features.torrentlist.impl.generated.resources.ic_link_24
import torrservermedia.features.torrentlist.impl.generated.resources.ic_settings_24

@Composable
internal fun MainAppBarUi(component: MainAppBarComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    AppTopBar(
        modifier = modifier,
        actions = {
            AppActionButton(
                enabled = uiState.value.isServerStarted,
                onClick = { component.onClickAddTorrent() },
                imageVector = vectorResource(Res.drawable.ic_add_24),
            )

            AppActionButton(
                enabled = uiState.value.isServerStarted,
                onClick = { component.openAddLinkDialog() },
                imageVector = vectorResource(Res.drawable.ic_link_24),
            )

            AppActionButton(
                enabled = uiState.value.isServerStarted,
                onClick = { component.openSettingsScreen() },
                imageVector = vectorResource(Res.drawable.ic_settings_24),
            )
        }
    )

    if (uiState.value.action == MainAppBarAction.ShowAddLinkDialog) {
        val errorLink = uiState.value.errorLink
        AddLlinkDialog(link = uiState.value.link,
            linkError = if (errorLink != null) stringResource(errorLink) else null,
            onDismissRequest = { component.dismissDialog() },
            onClickOkButton = { component.addLink() },
            onClickCancelButton = { component.dismissDialog() },
            onValueChange = { component.onLinkChaged(it) })
    }
}