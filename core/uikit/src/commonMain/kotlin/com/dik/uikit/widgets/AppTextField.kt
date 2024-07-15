package com.dik.uikit.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dik.uikit.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.ic_error_24

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTextField(
    value: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: (String) -> String? = { null },
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minHeight: Dp = Dp.Unspecified,
    ignoreSpaceIfItFirst: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember() {
        BringIntoViewRequester()
    }
    var textFieldIsFocused by remember {
        mutableStateOf(false)
    }
    var showError by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .then(modifier)
    ) {
        Column {
            Box {
                BasicTextField(
                    value = value,
                    textStyle = AppTheme.typography.normalText,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    enabled = enabled,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    onValueChange = {
                        if (ignoreSpaceIfItFirst && it.length == 1 && it == " ") {
                            onValueChange(it.trim())
                            return@BasicTextField
                        }
                        showError = false
                        onValueChange(it)
                    },
                    modifier = Modifier
                        .onFocusChanged {
                            textFieldIsFocused = it.isFocused
                        }
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                scope.launch {
                                    delay(200)
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        }
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                        .defaultMinSize(minHeight = minHeight)
//                        .testTag(C.Tag.text_field),
                )

                if (!error(value).isNullOrEmpty()) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_error_24),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .align(Alignment.CenterEnd)
                            .clickable { showError = !showError }
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .background(
                        if (textFieldIsFocused) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.outline
                    )
                    .height(2.dp)
                    .fillMaxWidth()
            )

            if (showError && !error(value).isNullOrEmpty()) {
                AppNormalText(
                    text = error(value) ?: "",
                )
            }
        }
        if (!placeholder.isNullOrEmpty() && value.isEmpty()) {
            AppNormalText(
                text = placeholder,
                style = AppTheme.typography.normalText.copy(color = MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, bottom = 16.dp)
            )
        }
    }
}