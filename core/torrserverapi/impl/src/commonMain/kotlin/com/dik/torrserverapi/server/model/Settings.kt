package com.dik.torrserverapi.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    @SerialName("cacheSize") val cacheSize: Long,
    @SerialName("readerReadAHead") val readerReadAHead: Int,
    @SerialName("preloadCache") val preloadCache: Int,
    @SerialName("enableIPv6") val enableIPv6: Boolean = false,
    @SerialName("disableTCP") val disableTCP: Boolean = false,
    @SerialName("disableUTP") val disableUTP: Boolean = false,
    @SerialName("disablePEX") val disablePEX: Boolean = false,
    @SerialName("forceEncrypt") val forceEncrypt: Boolean = false,
    @SerialName("torrentDisconnectTimeout") val torrentDisconnectTimeout: Int = 0,
    @SerialName("connectionsLimit") val connectionsLimit: Int = 0,
    @SerialName("disableDHT") val disableDHT: Boolean = false,
    @SerialName("DisableUPNP") val disableUpnp: Boolean = false,
    @SerialName("downloadRateLimit") val downloadRateLimit: Int = 0,
    @SerialName("disableUpload") val disableUpload: Boolean = false,
    @SerialName("uploadRateLimit") val uploadRateLimit: Int = 0,
    @SerialName("peersListenPort") val peersListenPort: Int = 0,
    @SerialName("EnableDLNA") val enableDLNA: Boolean = false,
    @SerialName("FriendlyName") val dlnaName: String = "",
)
