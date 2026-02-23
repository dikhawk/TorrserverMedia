package com.dik.torrentlist.screens.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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


@Composable
internal fun DetailsUi(
    component: DetailsComponent,
    poster: String,
    modifier: Modifier = Modifier,
) {
    val uiState by component.uiState.collectAsState()
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val headerHeight = 500.dp
    val headerHeightPx = with(density) { headerHeight.toPx() }

    val isVisibleAppBar by remember {
        derivedStateOf {
            scrollState.value > headerHeightPx
        }
    }
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isVisibleAppBar)
            AppTheme.colors.surface
        else
            Color.Transparent,
        label = "appBarColor"
    )

    Scaffold(
        topBar = {
            DetailsAppBar(
                color = animatedContainerColor,
                title = if (isVisibleAppBar) uiState.torrentName else "",
                onClickBack = { component.onClickBack() },
                onClickDeleteTorrent = { component.onClickDeleteTorrent() },
                modifier
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .verticalScroll(state = scrollState)
        ) {
            ImageBoard(headerHeight, poster, uiState.torrentName)

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

@Composable
private fun DetailsAppBar(
    color: Color,
    title: String,
    onClickBack: () -> Unit,
    onClickDeleteTorrent: () -> Unit,
    modifier: Modifier
) {
    AppTopBar(
        containerColor = color,
        title = title,
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    AppTheme.colors.surface.copy(alpha = 0.7f),
                    AppTheme.colors.surface.copy(alpha = 0.0f)
                ),
            ),
        ),
        navigationIcon = { AppIconButtonArrowBack { onClickBack() } }
    ) {
        IconButton(onClick = onClickDeleteTorrent, modifier = modifier) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_delete_24),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ImageBoard(
    headerHeight: Dp,
    poster: String,
    torrentName: String
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .heightIn(max = 800.dp)
    ) {
        AppAsyncImage(
            modifier = Modifier.widthIn(max = 800.dp)
                .heightIn(min = headerHeight)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .blur(radius = 56.dp),
            url = poster,
            contentScale = ContentScale.FillWidth,
        )

        AppAsyncImage(
            modifier = Modifier.widthIn(max = 800.dp)
                .heightIn(min = headerHeight)
                .fillMaxWidth()
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
                text = torrentName,
                modifier = Modifier,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        }
    }
}