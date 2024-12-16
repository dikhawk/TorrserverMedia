package com.dik.torrentlist.screens.details.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.AppNormaBoldlItalicText
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalHorizontalSpacer
import com.dik.uikit.widgets.AppNormalText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.ic_folder_24
import torrservermedia.features.torrentlist.impl.generated.resources.is_eye_24
import torrservermedia.features.torrentlist.impl.generated.resources.main_content_files_files_list

@Composable
internal fun ContentFilesUi(
    component: ContentFilesComponent,
    modifier: Modifier = Modifier
) {
    val uiState by component.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        AppNormalBoldText(
            text = stringResource(Res.string.main_content_files_files_list),
        )

        uiState.files.forEach { (directoryName, files) ->
            if (directoryName.isNotEmpty()) DirectoryItem(name = directoryName)

            files.forEach { file ->
                FileItem(
                    file = file,
                    directoryName = directoryName,
                    onClickItem = { component.onClickItem(file.contentFile) }
                )
            }
        }
    }
}

@Composable
private fun DirectoryItem(name: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(Res.drawable.ic_folder_24), contentDescription = null)
        AppNormalHorizontalSpacer()
        AppNormalBoldText(
            text = name,
            modifier = modifier.padding(top = 8.dp, bottom = 8.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FileItem(
    file: File,
    directoryName: String,
    onClickItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth().clickable { onClickItem() }.padding(8.dp)) {
        if (directoryName.isNotEmpty()) Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            AppNormalText(text = file.name)
            Row(modifier = Modifier.fillMaxWidth()) {
                AppNormaBoldlItalicText(text = file.size)
                Spacer(modifier = Modifier.weight(1f))
                if (file.contentFile.isViewed) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.is_eye_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}