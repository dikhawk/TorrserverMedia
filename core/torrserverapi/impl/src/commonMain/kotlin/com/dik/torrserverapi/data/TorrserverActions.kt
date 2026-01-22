package com.dik.torrserverapi.data

internal enum class TorrentsAction(val asString: String) {
    GET("get"),
    LIST("list"),
    ADD("add"),
    SET("set"),
    REM("rem"),
    DROP("drop"),
    WIPE("wipe"),
    DEFAULT("def"),
}