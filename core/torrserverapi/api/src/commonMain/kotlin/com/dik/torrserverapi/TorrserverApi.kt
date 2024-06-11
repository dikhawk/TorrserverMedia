package com.dik.torrserverapi

interface TorrserverApi {
    /**
     * Tests whether server is alive or not
     * return String server version
     */
    fun echo(): String

    fun getTorrentsList(): List<Torrent>

    fun addTorrent(torrent: Torrent)

    fun addMagnet(magnetUrl: String)
}