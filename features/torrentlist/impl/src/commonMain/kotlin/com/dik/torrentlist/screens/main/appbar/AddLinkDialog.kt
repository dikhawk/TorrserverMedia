package com.dik.torrentlist.screens.main.appbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.widgets.AppDialog
import com.dik.uikit.widgets.AppTextButton
import com.dik.uikit.widgets.AppTextField
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_button_cancel
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_button_ok
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_placeholder_past_link

@Composable
internal fun AddLinkDialog(
    link: String,
    modifier: Modifier = Modifier,
    linkError: String? = null,
    isShowProgress: Boolean = false,
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
                enabled = !isShowProgress,
                text = stringResource(Res.string.main_add_dialog_button_ok),
                onClick = onClickOkButton
            )
        }
    ) {
        AppTextField(
            error = { linkError },
            modifier = Modifier.fillMaxWidth(),
            isShowProgress = isShowProgress,
            placeholder = stringResource(Res.string.main_add_dialog_placeholder_past_link),
            value = link,
            onValueChange = onValueChange
        )
    }
}

@Preview
@Composable
private fun AddLinkDialogPreview() {
    AppTheme {
        AddLinkDialog(
            link = "magnet:Link",
            onValueChange = {}
        )
    }
}