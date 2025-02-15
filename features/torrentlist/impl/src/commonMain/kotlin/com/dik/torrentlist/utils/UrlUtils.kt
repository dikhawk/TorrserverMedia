package com.dik.torrentlist.utils

internal fun String.isValidMagnetLink(): Boolean {
    val magnetUriRegex = Regex("^magnet:\\?xt=urn:[a-z0-9]+:[a-zA-Z0-9]{32,40}(&.+)?$")
    return magnetUriRegex.matches(this)
}