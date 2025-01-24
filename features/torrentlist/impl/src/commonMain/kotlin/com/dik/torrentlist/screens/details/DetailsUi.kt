package com.dik.torrentlist.screens.details

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.components.bufferization.BufferizationUi
import com.dik.torrentlist.screens.details.files.ContentFilesUi
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsUI
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppIconButtonArrowBack
import com.dik.uikit.widgets.AppMiddleVerticalSpacer
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppTopBar
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.ic_delete_24


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.DetailsUi(
    component: DetailsComponent,
    torrentHash: String,
    poster: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val uiState by component.uiState.collectAsState()
    var isVisibleAppBar by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppTopBar(
                containerColor = if (isVisibleAppBar) AppTheme.colors.surface else Color.Transparent,
                title = if (isVisibleAppBar) uiState.torrentName else "",
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppTheme.colors.surface.copy(alpha = 0.7f),
                            AppTheme.colors.surface.copy(alpha = 0.0f)
                        ),
                    ),
                ),
                navigationIcon = { AppIconButtonArrowBack { component.onClickBack() } }
            ) {
                IconButton(onClick = { component.onClickDeleteTorrent() }, modifier = modifier) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_delete_24),
                        contentDescription = null,
                    )
                }
            }
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(max = 800.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        isVisibleAppBar = layoutCoordinates.size.height <= scrollState.value
                    }.sharedElementWithCallerManagedVisibility(
                        sharedContentState = rememberSharedContentState(key = torrentHash),
                        visible = isVisible
                    )
            ) {
                AppAsyncImage(
                    modifier = Modifier.widthIn(max = 800.dp).heightIn(min = 500.dp).fillMaxWidth()
                        .align(Alignment.BottomCenter).blur(radius = 56.dp),
                    url = poster,
                    contentScale = ContentScale.FillWidth,
                )

                AppAsyncImage(
                    modifier = Modifier.widthIn(max = 800.dp).heightIn(min = 500.dp).fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    url = poster,
                    contentScale = ContentScale.Fit,
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    AppTheme.colors.surface.copy(alpha = 0.4f),
                                    AppTheme.colors.surface.copy(alpha = 1.0f)
                                ),
                            ),
                        )
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    AppNormalBoldText(
                        text = uiState.torrentName,
                        modifier = Modifier,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                TorrentStatisticsUI(component.torrentStatisticsComponent)

                AppMiddleVerticalSpacer()

                AboutContentUi(
                    title = uiState.title,
                    overview = uiState.overview,
                    season = uiState.seasonNumber
                )

                AppMiddleVerticalSpacer()

                ContentFilesUi(
                    component = component.contentFilesComponent,
                )
            }
        }
    }

    if (uiState.isShowBufferization) {
        BufferizationUi(component = component.bufferizationComponent)
    }
}