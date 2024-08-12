package com.dik.settings.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dik.settings.widgets.ConfirmDialog
import com.dik.settings.widgets.DropDownListItem
import com.dik.settings.widgets.SwitchItem
import com.dik.settings.widgets.TextFieldItem
import com.dik.uikit.widgets.AppIconButtonArrowBack
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppTopBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.settings.impl.generated.resources.Res
import torrservermedia.features.settings.impl.generated.resources.ic_save_24
import torrservermedia.features.settings.impl.generated.resources.main_app_bar_title
import torrservermedia.features.settings.impl.generated.resources.main_settings_cache_size_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_cache_size_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_default_player_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_default_settings_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_dht_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_dht_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_dialog_default_settings_title
import torrservermedia.features.settings.impl.generated.resources.main_settings_distribution_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_distribution_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_dlna_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_dlna_name_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_encryption_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_encryption_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_ipv6_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_ipv6_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_limit_speed_distribution_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_limit_speed_distribution_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_limit_speed_download_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_limit_speed_download_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_mtp_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_mtp_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_pex_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_pex_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_port_incoming_connection_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_port_incoming_connection_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_preload_cache_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_preload_cache_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_reader_read_a_head_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_reader_read_a_head_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_tcp_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_tcp_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_timeout_connection_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_timeout_connection_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_torrent_connections_header
import torrservermedia.features.settings.impl.generated.resources.main_settings_torrent_connections_hint
import torrservermedia.features.settings.impl.generated.resources.main_settings_upnp_header

@Composable
internal fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()
    val scrollstate = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                navigationIcon = { AppIconButtonArrowBack { component.onClickBack() } },
                title = stringResource(Res.string.main_app_bar_title),
                actions = {
                    IconButton(onClick = { component.onClickSave() }, modifier = modifier) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_save_24),
                            contentDescription = null,
                        )
                    }
                    if (uiState.value.isShowProgressBar)
                        CircularProgressIndicator(modifier = modifier.height(24.dp).width(24.dp))
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp).verticalScroll(scrollstate)) {
            Spacer(modifier = Modifier.height(64.dp))

            DropDownListItem(
                header = stringResource(Res.string.main_settings_default_player_header),
                selectedItem = uiState.value.deafaultPlayer,
                items = uiState.value.playersList,
                title = { it.title },
                onClickItem = { component.onChangeDefaultPlayer(it) })

            TextFieldItem(
                header = stringResource(Res.string.main_settings_cache_size_header),
                hint = "${uiState.value.cacheSize} " +
                        "${stringResource(Res.string.main_settings_cache_size_hint)}",
                value = uiState.value.cacheSize.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeCacheSize(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_reader_read_a_head_header),
                hint = "${uiState.value.readerReadAHead} " +
                        "${stringResource(Res.string.main_settings_reader_read_a_head_hint)}",
                value = uiState.value.readerReadAHead.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeReaderReadAHead(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_preload_cache_header),
                hint = "${uiState.value.preloadCache} " +
                        "${stringResource(Res.string.main_settings_preload_cache_hint)}",
                value = uiState.value.preloadCache.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangePreloadCache(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_ipv6_header),
                hint = stringResource(Res.string.main_settings_ipv6_hint),
                checked = uiState.value.ipv6,
                onCheckedChange = { component.onChangeIpv6(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_tcp_header),
                hint = stringResource(Res.string.main_settings_tcp_hint),
                checked = uiState.value.tcp,
                onCheckedChange = { component.onChangeTcp(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_mtp_header),
                hint = stringResource(Res.string.main_settings_mtp_hint),
                checked = uiState.value.mtp,
                onCheckedChange = { component.onChangeMtp(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_pex_header),
                hint = stringResource(Res.string.main_settings_pex_hint),
                checked = uiState.value.pex,
                onCheckedChange = { component.onChangePex(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_encryption_header),
                hint = stringResource(Res.string.main_settings_encryption_hint),
                checked = uiState.value.encryptionHeader,
                onCheckedChange = { component.onChangeEncryptionHeader(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_timeout_connection_header),
                hint = "${uiState.value.timeoutConnection} " +
                        "${stringResource(Res.string.main_settings_timeout_connection_hint)}",
                value = uiState.value.timeoutConnection.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeTimeoutConnection(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_torrent_connections_header),
                hint = "${uiState.value.torrentConnections}, " +
                        "${stringResource(Res.string.main_settings_torrent_connections_hint)}",
                value = uiState.value.torrentConnections.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeTorrentConnections(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_dht_header),
                hint = stringResource(Res.string.main_settings_dht_hint),
                checked = uiState.value.dht,
                onCheckedChange = { component.onChangeDht(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_limit_speed_download_header),
                hint = "${uiState.value.limitSpeedDownload} " +
                        "${stringResource(Res.string.main_settings_limit_speed_download_hint)}",
                value = uiState.value.limitSpeedDownload.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeLimitSpeedDownload(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_distribution_header),
                hint = stringResource(Res.string.main_settings_distribution_hint),
                checked = uiState.value.distribution,
                onCheckedChange = { component.onChangeDistribution(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_limit_speed_distribution_header),
                hint = "${uiState.value.limitSpeedDistribution} " +
                        "${stringResource(Res.string.main_settings_limit_speed_distribution_hint)}",
                value = uiState.value.limitSpeedDistribution.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeLimitSpeedDistribution(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_port_incoming_connection_header),
                hint = "${uiState.value.incomingConnection} " +
                        "${stringResource(Res.string.main_settings_port_incoming_connection_hint)}",
                value = uiState.value.incomingConnection.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { component.onChangeIncomingConnection(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_upnp_header),
                checked = uiState.value.upnp,
                onCheckedChange = { component.onChangeUpnp(it) }
            )

            SwitchItem(
                header = stringResource(Res.string.main_settings_dlna_header),
                checked = uiState.value.dlna,
                onCheckedChange = { component.onChangeDlna(it) }
            )

            TextFieldItem(
                header = stringResource(Res.string.main_settings_dlna_name_header),
                hint = uiState.value.dlnaName,
                value = uiState.value.dlnaName,
                onValueChange = { component.onChangeDlnaName(it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { component.invokeAction(MainAction.DEFAULT_SETTINGS_DIALOG) }
                    .padding(8.dp)) {
                AppNormalText(text = stringResource(Res.string.main_settings_default_settings_header))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                AppNormalText(uiState.value.operationSystem)
            }
        }
    }

    if (uiState.value.action == MainAction.DEFAULT_SETTINGS_DIALOG) {
        ConfirmDialog(
            message = stringResource(Res.string.main_settings_dialog_default_settings_title),
            onDismissRequest = { component.dismissAction() },
            onClickCancelButton = { component.dismissAction() },
            onClickOkButton = {
                component.defaultSettings()
                component.dismissAction()
            }
        )
    }

    LaunchedEffect(uiState.value.snackbar) {
        val message = uiState.value.snackbar

        if (!message.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(message = message)
            component.dismissSnackbar()
        }
    }
}