package com.dik.torrentlist.screens.main.appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dik.torrentlist.screens.main.appbar.utils.defaultFilePickerDirectory
import com.dik.torrentlist.utils.toPath
import com.dik.uikit.widgets.AppActionButton
import com.dik.uikit.widgets.AppTopBar
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.async
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.add_torrent_dialog_title
import torrservermedia.features.torrentlist.impl.generated.resources.ic_add_24
import torrservermedia.features.torrentlist.impl.generated.resources.ic_link_24
import torrservermedia.features.torrentlist.impl.generated.resources.ic_settings_24
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_title

@Composable
internal fun MainAppBarUi(component: MainAppBarComponent, modifier: Modifier = Modifier) {
    val uiState by component.uiState.collectAsState()

    AppTopBar(
        title = stringResource(Res.string.main_app_bar_title),
        modifier = modifier,
        actions = {
            AppActionButton(
                enabled = uiState.isServerStarted,
                onClick = { component.openFilePickTorrent() },
                imageVector = vectorResource(Res.drawable.ic_add_24),
            )

            AppActionButton(
                enabled = uiState.isServerStarted,
                onClick = { component.openAddLinkDialog() },
                imageVector = vectorResource(Res.drawable.ic_link_24),
            )

            AppActionButton(
                enabled = uiState.isServerStarted,
                onClick = { component.openSettingsScreen() },
                imageVector = vectorResource(Res.drawable.ic_settings_24),
            )
        }
    )

    if (uiState.action == MainAppBarAction.ShowAddLinkDialog) {
        val errorLink = uiState.errorLink
        AddLlinkDialog(link = uiState.link,
            linkError = errorLink,
            onDismissRequest = { component.dismissDialog() },
            onClickOkButton = { component.addLink() },
            onClickCancelButton = { component.dismissDialog() },
            onValueChange = { component.onLinkChanged(it) })
    }


    LaunchedEffect(uiState.action) {
        if (uiState.action == MainAppBarAction.ShowFilePicker) {
            val result = async {
                val fileType = PickerType.File(extensions = listOf("torrent"))
                FileKit.pickFile(
                    type = fileType,
                    mode = PickerMode.Single,
                    title = getString(Res.string.add_torrent_dialog_title),
                    initialDirectory = defaultFilePickerDirectory()
                )
            }
            val path = result.await()?.toPath()

            if (path != null) {
                component.onFilePicked(path)
            }

            component.dismissDialog()
        }
    }
}