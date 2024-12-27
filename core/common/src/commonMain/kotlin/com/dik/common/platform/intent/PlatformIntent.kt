package com.dik.common.platform.intent

data class PlatformIntent(
    val action: PlatformAction,
    val data: Any? = null
)

enum class PlatformAction {
    ADD_TORRENT,
}