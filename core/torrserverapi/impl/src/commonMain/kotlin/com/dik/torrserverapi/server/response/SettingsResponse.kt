package com.dik.torrserverapi.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsResponse(
    @SerialName("CacheSize")
    val cacheSize: Long,

    @SerialName("ReaderReadAHead")
    val readerReadAHead: Int,

    @SerialName("PreloadCache")
    val preloadCache: Int,

    @SerialName("UseDisk")
    val useDisk: Boolean,

    @SerialName("TorrentsSavePath")
    val torrentsSavePath: String,

    @SerialName("RemoveCacheOnDrop")
    val removeCacheOnDrop: Boolean,

    @SerialName("ForceEncrypt")
    val forceEncrypt: Boolean,

    @SerialName("RetrackersMode")
    val retrackersMode: Int,

    @SerialName("TorrentDisconnectTimeout")
    val torrentDisconnectTimeout: Int,

    @SerialName("EnableDebug")
    val enableDebug: Boolean,

    @SerialName("EnableDLNA")
    val enableDLNA: Boolean,

    @SerialName("FriendlyName")
    val friendlyName: String,

    @SerialName("EnableRutorSearch")
    val enableRutorSearch: Boolean,

    @SerialName("EnableIPv6")
    val enableIPv6: Boolean,

    @SerialName("DisableTCP")
    val disableTCP: Boolean,

    @SerialName("DisableUTP")
    val disableUTP: Boolean,

    @SerialName("DisableUPNP")
    val disableUPNP: Boolean,

    @SerialName("DisableDHT")
    val disableDHT: Boolean,

    @SerialName("DisablePEX")
    val disablePEX: Boolean,

    @SerialName("DisableUpload")
    val disableUpload: Boolean,

    @SerialName("DownloadRateLimit")
    val downloadRateLimit: Int,

    @SerialName("UploadRateLimit")
    val uploadRateLimit: Int,

    @SerialName("ConnectionsLimit")
    val connectionsLimit: Int,

    @SerialName("PeersListenPort")
    val peersListenPort: Int,

    @SerialName("SslPort")
    val sslPort: Int,

    @SerialName("SslCert")
    val sslCert: String,

    @SerialName("SslKey")
    val sslKey: String,

    @SerialName("ResponsiveMode")
    val responsiveMode: Boolean
)