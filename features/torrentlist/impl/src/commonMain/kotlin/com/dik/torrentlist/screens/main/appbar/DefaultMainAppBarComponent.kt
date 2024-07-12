package com.dik.torrentlist.screens.main.appbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.common.AppDispatchers
import com.dik.torrentlist.screens.main.appbar.utils.defaultFilePickerDirectory
import com.dik.torrserverapi.server.TorrentApi
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_title

internal class DefaultMainAppBarComponent(
    context: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val torrentApi: TorrentApi
) : MainAppBarComponent, ComponentContext by context {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())

    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
    }

    override fun onClickAddTorrent() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            val fileType = PickerType.File(extensions = listOf("torrent"))

            val file = FileKit.pickFile(
                type = fileType,
                mode = PickerMode.Single,
                title = getString(Res.string.main_app_bar_title),
                initialDirectory = defaultFilePickerDirectory()
            )
            val filePath = file?.path

            if (filePath != null) torrentApi.addTorrent(filePath)
        }
    }

    override fun onClickAddLink() {

    }
}