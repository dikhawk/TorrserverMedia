package com.dik.uikit.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.uikit_simple_dialog_button_cancel
import torrservermedia.core.uikit.generated.resources.uikit_simple_dialog_button_ok


@Composable
fun AppSimpleDialog(
    modifier: Modifier = Modifier,
    title: String = "",
    message: String,
    onClickCancel: (() -> Unit)? = null,
    onClickOkButton: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    AppDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        buttons = {
            if (onClickCancel != null) {
                AppTextButton(
                    text = stringResource(Res.string.uikit_simple_dialog_button_cancel),
                    onClick = onClickCancel
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            AppTextButton(
                text = stringResource(Res.string.uikit_simple_dialog_button_ok),
                onClick = onClickOkButton
            )
        },
        content = {
            Column(modifier.fillMaxWidth()) {
                if (title.isNotEmpty()) {
                    AppNormalBoldText(text = title)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                AppNormalText(message)
            }
        }
    )
}

