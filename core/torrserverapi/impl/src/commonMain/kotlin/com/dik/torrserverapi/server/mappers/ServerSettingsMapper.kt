package com.dik.torrserverapi.server.mappers

import com.dik.torrserverapi.model.ServerSettings
import com.dik.torrserverapi.server.model.Settings
import com.dik.torrserverapi.server.response.SettingsResponse

fun SettingsResponse.mapToServerSettings() = ServerSettings(
    cacheSize = this.cacheSize,
    readerReadAHead = this.readerReadAHead,
    preloadCache = this.preloadCache,
    ipv6 = this.enableIPv6,
    tcp = !this.disableTCP,
    mtp = !this.disableUTP,
    pex = !this.disablePEX,
    encryptionHeader = this.forceEncrypt,
    timeoutConnection = this.torrentDisconnectTimeout,
    torrentConnections = this.connectionsLimit,
    dht = !this.disableDHT,
    limitSpeedDownload = this.downloadRateLimit,
    distribution = !this.disableUpload,
    limitSpeedDistribution = this.uploadRateLimit,
    incomingConnection = this.peersListenPort,
    dlnaName = this.friendlyName
)

//TODO rename fields
fun ServerSettings.mapToSettings() = Settings(
    cacheSize = this.cacheSize,
    readerReadAHead = this.readerReadAHead,
    preloadCache = this.preloadCache,
    enableIPv6 = this.ipv6,
    disableTCP = !this.tcp,
    disableUTP = !this.mtp,
    disablePEX = !this.pex,
    forceEncrypt = this.encryptionHeader,
    torrentDisconnectTimeout = this.timeoutConnection,
    connectionsLimit = this.torrentConnections,
    disableDHT = !this.dht,
    downloadRateLimit = this.limitSpeedDownload,
    disableUpload = !this.distribution,
    uploadRateLimit = this.limitSpeedDistribution,
    peersListenPort = this.incomingConnection,
)