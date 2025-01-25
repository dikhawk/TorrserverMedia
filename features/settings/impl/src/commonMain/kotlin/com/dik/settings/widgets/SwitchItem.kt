package com.dik.settings.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppSmallText

@Composable
internal fun SwitchItem(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    header: String,
    hint: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clickable { onCheckedChange?.invoke(!checked) }
            .padding(8.dp),
    ) {
        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                AppNormalText(header)
                if (hint.isNotEmpty()) AppSmallText(hint)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}