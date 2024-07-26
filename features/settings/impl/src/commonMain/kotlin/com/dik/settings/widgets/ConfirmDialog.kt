package com.dik.settings.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dik.uikit.widgets.AppDialog
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppTextButton
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.settings.impl.generated.resources.Res
import torrservermedia.features.settings.impl.generated.resources.main_settings_dialog_button_cancel
import torrservermedia.features.settings.impl.generated.resources.main_settings_dialog_button_ok

@Composable
fun ConfirmDialog(
    message: String,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onClickOkButton: () -> Unit = {},
    onClickCancelButton: () -> Unit = {},
) {
    AppDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        buttons = {
            AppTextButton(
                text = stringResource(Res.string.main_settings_dialog_button_cancel),
                onClick = onClickCancelButton
            )
            AppTextButton(
                text = stringResource(Res.string.main_settings_dialog_button_ok),
                onClick = onClickOkButton
            )
        }
    ) {
        AppNormalText(message)
    }
}