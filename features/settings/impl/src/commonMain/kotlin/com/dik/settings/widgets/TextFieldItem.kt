package com.dik.settings.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppSmallText
import com.dik.uikit.widgets.AppTextField
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.settings.impl.generated.resources.Res
import torrservermedia.features.settings.impl.generated.resources.ic_done_24

@Composable
fun TextFieldItem(
    header: String,
    hint: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val isVisibleTextTextField = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
            .then(if (isVisibleTextTextField.value) modifier else modifier.clickable {
                isVisibleTextTextField.value = true
            })
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                AppNormalText(header)
                when {
                    isVisibleTextTextField.value -> TexField(
                        value = value,
                        onValueChange = onValueChange,
                        isVisibleTextTextField = isVisibleTextTextField,
                        keyboardOptions = keyboardOptions
                    )

                    !isVisibleTextTextField.value && !hint.isNullOrEmpty() -> AppSmallText(hint)
                }
            }
        }
    }
}

@Composable
private fun TexField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisibleTextTextField: MutableState<Boolean>,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions
        )
        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { isVisibleTextTextField.value = false }) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_done_24),
                contentDescription = null
            )
        }
    }
}