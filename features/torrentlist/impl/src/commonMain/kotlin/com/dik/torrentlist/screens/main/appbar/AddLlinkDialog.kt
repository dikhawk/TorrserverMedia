package com.dik.torrentlist.screens.main.appbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dik.uikit.widgets.AppDialog
import com.dik.uikit.widgets.AppTextButton
import com.dik.uikit.widgets.AppTextField
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_button_cancel
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_button_ok
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_placeholder_past_link

@Composable
internal fun AddLlinkDialog(
    link: String,
    modifier: Modifier = Modifier,
    linkError: String? = null,
    onClickOkButton: () -> Unit = {},
    onClickCancelButton: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onValueChange: (String) -> Unit,
) {
    AppDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        buttons = {
            AppTextButton(
                text = stringResource(Res.string.main_add_dialog_button_cancel),
                onClick = onClickCancelButton
            )
            AppTextButton(
                text = stringResource(Res.string.main_add_dialog_button_ok),
                onClick = onClickOkButton
            )
        }
    ) {
        AppTextField(
            error = { linkError },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.main_add_dialog_placeholder_past_link),
            value = link,
            onValueChange = onValueChange
        )
    }
}