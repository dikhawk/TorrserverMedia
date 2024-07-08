package com.dik.torrserverapi.server

enum class TorrentsAction(val asString: String) {
    GET("get"),
    LIST("list"),
    ADD("add"),
    SET("set"),
    REM("rem"),
    DROP("drop"),
    WIPE("wipe"),
}