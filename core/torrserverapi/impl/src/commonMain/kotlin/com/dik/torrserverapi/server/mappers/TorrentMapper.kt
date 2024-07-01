package com.dik.torrserverapi.server.mappers

import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.Torrent
import com.dik.torrserverapi.server.response.ContentFileResponse
import com.dik.torrserverapi.server.response.TorrentResponse

fun TorrentResponse.mapToTorrent(): Torrent = Torrent(
    hash = this.hash ?: "",
    title = this.title ?: "",
    poster = this.poster ?: "",
    name = this.name ?: "",
    files = this.fileStats.mapToConetentFileList()
)

fun List<TorrentResponse>.mapToTorrentList(): List<Torrent> = map { it.mapToTorrent() }

fun ContentFileResponse.maptoContentFile(): ContentFile = ContentFile(
    id = this.id ?: 0,
    path = this.path ?: "",
    length = this.length ?: 0
)

fun List<ContentFileResponse>.mapToConetentFileList(): List<ContentFile> = map {
    it.maptoContentFile()
}