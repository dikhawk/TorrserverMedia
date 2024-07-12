package com.dik.torrentlist.screens.main.appbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dik.uikit.widgets.AppActionButton
import com.dik.uikit.widgets.AppTopBar
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.ic_add_24
import torrservermedia.features.torrentlist.impl.generated.resources.ic_link_24dp

@Composable
internal fun MainAppBarUi(component: MainAppBarComponent, modifier: Modifier = Modifier) {
    AppTopBar(
        actions = {
            AppActionButton(
                onClick = { component.onClickAddTorrent() },
                imageVector = vectorResource(Res.drawable.ic_add_24),
            )

            AppActionButton(
                onClick = { component.onClickAddLink() },
                imageVector = vectorResource(Res.drawable.ic_link_24dp),
            )
        }
    )
}