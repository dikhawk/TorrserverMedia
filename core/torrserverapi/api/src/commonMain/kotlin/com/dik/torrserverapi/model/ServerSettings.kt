package com.dik.torrserverapi.model

data class ServerSettings(
    val cacheSize: Long,
    val readerReadAHead: Int,
    val preloadCache: Int,
    val ipv6: Boolean = false,
    val tcp: Boolean = false,
    val mtp: Boolean = false,
    val pex: Boolean = false,
    val encryptionHeader: Boolean = false,
    val timeoutConnection: Int = 0,
    val torrentConnections: Int = 0,
    val dht: Boolean = false,
    val limitSpeedDownload: Int = 0,
    val distribution: Boolean = false,
    val limitSpeedDistribution: Int = 0,
    val incomingConnection: Int = 0,
    val upnp: Boolean = false,
    val dlna: Boolean = false,
    val dlnaName: String = "",
)
