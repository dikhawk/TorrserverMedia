package com.dik.torrentlist.error

import com.dik.torrserverapi.TorrserverError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_no_server_connection
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_platform_not_supported
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_resoponse_return_null
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_server_file_not_exist
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_server_not_started

suspend fun TorrserverError.toMessage(): String {
    return when (this) {
        TorrserverError.HttpError.ResponseReturnNull ->
            Res.string.main_error_msg_resoponse_return_null.asString()
        is TorrserverError.Server.FileNotExist ->
            Res.string.main_error_msg_server_file_not_exist.asString()
        TorrserverError.Server.NoServerConnection ->
            Res.string.main_error_msg_no_server_connection.asString()
        TorrserverError.Server.NotStarted ->
            Res.string.main_error_msg_server_not_started.asString()
        is TorrserverError.Server.PlatformNotSupported ->
            Res.string.main_error_msg_platform_not_supported.asString()
        is TorrserverError.Unknown -> this.message
        is TorrserverError.HttpError.ResponseReturnError -> this.message
    }
}

private suspend fun StringResource.asString(): String = getString(this)