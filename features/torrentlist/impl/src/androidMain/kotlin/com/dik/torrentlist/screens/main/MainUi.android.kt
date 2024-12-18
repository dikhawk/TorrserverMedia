package com.dik.torrentlist.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dik.uikit.widgets.AppSimpleDialog
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_close_app_message
import kotlin.system.exitProcess

@Composable
internal actual fun CloseApp() {
    var isShowDialog by remember { mutableStateOf(false) }
    val closeDialog = remember { { isShowDialog = false } }

    BackHandler {
        isShowDialog = true
    }

    if (isShowDialog) {
        AppSimpleDialog(
            message = stringResource(Res.string.main_close_app_message),
            onClickOkButton = { exitProcess(0) },
            onDismissRequest = closeDialog,
            onClickCancel = closeDialog
        )
    }
}