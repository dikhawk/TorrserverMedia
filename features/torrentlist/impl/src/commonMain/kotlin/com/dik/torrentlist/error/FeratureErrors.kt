package com.dik.torrentlist.error

import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.domain.FindPosterErrors
import com.dik.torrserverapi.TorrserverError
import org.jetbrains.compose.resources.StringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_no_server_connection
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_platform_not_supported
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_poster_not_found
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_response_return_null
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_server_file_not_exist
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_server_not_started
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_title_is_empty
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_wrong_server_config

internal suspend fun TorrserverError.toMessage(localization: LocalizationResource): String {
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
        is TorrserverError.Server.WrongConfiguration ->
            Res.string.main_error_msg_wrong_server_config.asString(localization)
    }
}

internal suspend fun FindPosterErrors.toMessage(localization: LocalizationResource): String {
    return when (this) {
        FindPosterErrors.PosterNotFound -> Res.string.main_error_msg_poster_not_found.asString(localization)
        FindPosterErrors.TitleIsEmpty -> Res.string.main_error_msg_title_is_empty.asString(localization)
        is FindPosterErrors.UnknownError -> this.error
    }
}

private suspend fun StringResource.asString(localization: LocalizationResource): String =
    localization.getString(this)