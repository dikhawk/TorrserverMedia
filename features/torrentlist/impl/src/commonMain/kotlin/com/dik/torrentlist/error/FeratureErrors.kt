package com.dik.torrentlist.error

import com.dik.common.i18n.LocalizationResource
import com.dik.torrserverapi.TorrserverError
import org.jetbrains.compose.resources.StringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_no_server_connection
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_platform_not_supported
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_response_return_null
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_server_file_not_exist
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_server_not_started

suspend fun TorrserverError.toMessage(localization: LocalizationResource): String {
    return when (this) {
        TorrserverError.HttpError.ResponseReturnNull ->
            Res.string.main_error_msg_response_return_null.asString(localization)
        is TorrserverError.Server.FileNotExist ->
            Res.string.main_error_msg_server_file_not_exist.asString(localization)
        TorrserverError.Server.NoServerConnection ->
            Res.string.main_error_msg_no_server_connection.asString(localization)
        TorrserverError.Server.NotStarted ->
            Res.string.main_error_msg_server_not_started.asString(localization)
        is TorrserverError.Server.PlatformNotSupported ->
            Res.string.main_error_msg_platform_not_supported.asString(localization)
        is TorrserverError.Unknown -> this.message
        is TorrserverError.HttpError.ResponseReturnError -> this.message
    }
}

private suspend fun StringResource.asString(localization: LocalizationResource): String =
    localization.getString(this)