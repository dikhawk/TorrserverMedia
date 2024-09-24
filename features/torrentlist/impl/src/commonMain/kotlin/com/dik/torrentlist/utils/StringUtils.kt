package com.dik.torrentlist.utils

internal fun String.fileName(): String {
    return split("/").last()
}