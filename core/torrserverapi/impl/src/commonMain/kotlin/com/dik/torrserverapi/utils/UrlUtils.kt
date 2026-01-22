package com.dik.torrserverapi.utils

import com.dik.torrserverapi.SettingsConst
import io.ktor.http.encodeURLQueryComponent

object UrlUtils {
    fun getPlayLink(fileName: String, hash: String, index: Int): String {
        val name = fileName.encodeURLQueryComponent()
        return "${SettingsConst.LOCAL_TORRENT_SERVER}/stream/${name}?link=${hash}&index=${index}&play"
    }
}